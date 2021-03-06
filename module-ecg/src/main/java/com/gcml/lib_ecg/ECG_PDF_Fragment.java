package com.gcml.lib_ecg;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.gcml.lib_ecg.base.BluetoothBaseFragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.link.LinkHandler;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnLongPressListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.model.LinkTapEvent;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.gzq.lib_core.utils.DataUtils;
import com.gzq.lib_core.utils.FileUtils;
import com.gzq.lib_core.utils.ThreadUtils;
import com.gzq.lib_core.utils.TimeUtils;
import com.gzq.lib_core.utils.ToastUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;


public class ECG_PDF_Fragment extends BluetoothBaseFragment {
    public static final String KEY_BUNDLE_PDF_URL = "key_pdf_url";
    private View view;
    private PDFView mPdfView;
    private String url_pdf;
    private boolean isDestroy = false;

    @Override
    protected int initLayout() {
        return R.layout.bluetooth_fragment_pdf;
    }

    private final ThreadUtils.SimpleTask<File> pdfDownload = new ThreadUtils.SimpleTask<File>() {
        @Nullable
        @Override
        public File doInBackground() {
            InputStream input = null;
            FileOutputStream fos = null;
            File pdfFile = null;
            try {
                URL url = new URL(url_pdf);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                if (HttpURLConnection.HTTP_OK != connection.getResponseCode()) {
                    return null;
                }
                String diskCacheDir = FileUtils.getDiskCacheDir(getContext());
                pdfFile = new File(diskCacheDir,
                        "gcml_" + TimeUtils.getCurTimeString(new SimpleDateFormat("yyyyMMddHHmm")));
                FileUtils.createOrExistsFile(pdfFile);
                input = new BufferedInputStream(connection.getInputStream());
                fos = new FileOutputStream(pdfFile);

                int count;
                byte buf[] = new byte[1024];
                while ((count = input.read(buf)) != -1) {
                    if (isDestroy) {
                        cancel();
                        break;
                    }
                    fos.write(buf, 0, count);
                }
                return pdfFile;
            } catch (IOException e) {
                e.printStackTrace();
                if (pdfFile != null && pdfFile.exists()) {
                    pdfFile.delete();
                }
            } finally {
                if (null != fos) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != input) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        public void onSuccess(@Nullable File result) {
            if (result != null) {
                mPdfView.fromFile(result)
//                        .pages(0, 2,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   1, 3, 3, 3) //指定显示某页
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        // allows to draw something on the current page, usually visible in the middle of the screen
                        .onDraw(new OnDrawListener() {
                            @Override
                            public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                            }
                        })
                        .onDrawAll(new OnDrawListener() {
                            @Override
                            public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                            }
                        })
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {

                            }
                        })
                        .onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {

                            }
                        })
                        .onPageScroll(new OnPageScrollListener() {
                            @Override
                            public void onPageScrolled(int page, float positionOffset) {

                            }
                        })
                        .onError(new OnErrorListener() {
                            @Override
                            public void onError(Throwable t) {

                            }
                        })
                        .onPageError(new OnPageErrorListener() {
                            @Override
                            public void onPageError(int page, Throwable t) {

                            }
                        })
                        .onRender(new OnRenderListener() {
                            @Override
                            public void onInitiallyRendered(int nbPages) {

                            }
                        })
                        .onTap(new OnTapListener() {
                            @Override
                            public boolean onTap(MotionEvent e) {
                                return false;
                            }
                        })
                        .onLongPress(new OnLongPressListener() {
                            @Override
                            public void onLongPress(MotionEvent e) {

                            }
                        })
                        .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                        .password(null)
                        .scrollHandle(null)
                        .enableAntialiasing(true)
                        .spacing(0)
                        .autoSpacing(false)
                        .linkHandler(new LinkHandler() {
                            @Override
                            public void handleLinkEvent(LinkTapEvent event) {

                            }
                        })
                        .pageFitPolicy(FitPolicy.WIDTH)
                        .pageSnap(true) // snap pages to screen boundaries
                        .pageFling(false) // make a fling change only a single page like ViewPager
                        .nightMode(false) // toggle night mode
                        .load();
            }
        }
    };

    @Override
    protected void initView(View view, Bundle bundle) {
        mPdfView = view.findViewById(R.id.pdfView);
        if (bundle != null) {
            url_pdf = bundle.getString(KEY_BUNDLE_PDF_URL);
            if (DataUtils.isNullString(url_pdf)) {
                ToastUtils.showShort("资源文件不存在");
                return;
            }
            ThreadUtils.executeByIo(pdfDownload);
        } else {
            ToastUtils.showShort("资源不存在");
        }

    }

    @Override
    public void onDestroyView() {
        isDestroy = true;
        ThreadUtils.cancel(pdfDownload);
        super.onDestroyView();
    }
}
