package com.example.han.referralproject.new_music;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MusicPlayActivity extends AppCompatActivity {
    private Music music;
    private MusicService musicService;
    private MusicFragment musicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在刚进入此页面的时候就绑定音乐播放服务
        bindService(new Intent(this, MusicService.class), serviceConnection, BIND_AUTO_CREATE);
        music = (Music) getIntent().getSerializableExtra("music");
        CoverLoader.getInstance().init(this);
        showPlayingFragment();
    }

    private void showPlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.mp_fragment_slide_up, 0);
        musicFragment = new MusicFragment();
        ft.add(android.R.id.content, musicFragment).commit();
    }

    /**
     * binding音乐播放服务
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicBind) service).getService();
            onMusicServiceConnected(musicService);
            //绑定好以后把要播放的文件set到服务中去
            musicService.setMusicResourse(music);
            //并且把服务set到Fragment中去
            musicFragment.setMusicService(musicService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            onMusicServiceDisconnected();
        }
    };

    private void onMusicServiceDisconnected() {
        if (musicService != null) {
            musicService.setOnAudioFocusChangeListener(null);
            musicService.release();
            musicService = null;
        }
    }

    private void onMusicServiceConnected(MusicService musicService) {
        if (musicService == null) {
            return;
        }
        musicService.setOnAudioFocusChangeListener(new MusicService.AudioChange() {
            @Override
            public void onAudioFocusChange(AudioManager manager, int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    // Pause playback
                    if (musicFragment.ivPlay.isSelected()) {
                        musicFragment.ivPlay.performClick();
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // Resume playback
                    if (!musicFragment.ivPlay.isSelected()) {
                        musicFragment.ivPlay.performClick();
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    // Stop playback
                    if (musicFragment.ivPlay.isSelected()) {
                        musicFragment.ivPlay.performClick();
                    }
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (musicFragment.ivPlay.isSelected()) {
            musicFragment.ivPlay.performClick();
        }
    }

    @Override
    protected void onDestroy() {
        //取消绑定的服务
        if (serviceConnection != null)
            unbindService(serviceConnection);
        super.onDestroy();
    }
}
