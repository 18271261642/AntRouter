package com.jkcq.antrouter.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author ck
 * @version 创建时间：2016年10月17日 下午3:42:25 说明: 图片处理类
 */
public class BitmapUtils {
    protected int mScreenWidth;

    /**
     * DESC : 将字符串转换为Bitmap . <br/>
     *
     * @param string
     * @return Bitmap
     */
    public static Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * DESC : 讲Bitmap转换为字符串. <br/>
     *
     * @param bitmap
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static String bitmaptoString(Bitmap bitmap)
            throws UnsupportedEncodingException {
        // 将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 10, bStream);
        byte[] bytes = bStream.toByteArray();
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        string = new String(encode, "UTF-8");
        return string;
    }

    /**
     * <p>
     * 将文件转成base64 字符串
     * </p>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
        return new String(encode, "UTF-8");
    }

    /**
     * <p>
     * 将base64字符解码保存文件
     * </p>
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code, String targetPath)
            throws Exception {
        byte[] decode = base64Code.getBytes("UTF-8");
        byte[] buffer = Base64.decode(decode, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * <p>
     * 将base64字符保存文本文件
     * </p>
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void toFile(String base64Code, String targetPath)
            throws Exception {
        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * DESC : 讲bitmap写入到文件中 . <br/>
     *
     * @param bitmap
     * @param path
     * @throws Exception
     */
    public static void saveBitmapFile(Bitmap bitmap, String path)
            throws Exception {
        File file = new File(path);// 将要保存图片的路径
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(file));
        bitmap.compress(CompressFormat.PNG, 100, bos);
        bos.flush();
        bos.close();

        Logger.e("saveBitmapFile path = " + path + " ,size = " + file.length() / 1024);
    }

    /**
     * DESC : . <br/>
     *
     * @param bitmap
     * @return
     * @throws Exception
     */
    public static String getStringFromBitmap(Bitmap bitmap, String path)
            throws Exception {
        File file = new File(path);
        saveBitmapFile(bitmap, file.toString());
        String str = encodeBase64File(file.toString());
        if (file.exists()) {
//            file.delete();
        }
        return str;
    }

    /**
     * 将ImageView 转成String
     *
     * @param imageView
     * @return
     */
//    public static String getImageViewStr(ImageView imageView) {
//        String path = null;
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        } else {
//            path = Environment.getDownloadCacheDirectory().getAbsolutePath();
//        }
//        path = path + "/jkcq/fit/logo";
//        File file = new File(path);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        path = file.getAbsolutePath();
//        String pathTemp = path + "/1.JPEG";
//        Bitmap btm = changeToBitMap(imageView);
//        String bitmapString;
//        try {
//            BitmapUtils.saveBitmapFile(btm, pathTemp);
//            bitmapString = BitmapUtils.encodeBase64File(pathTemp);
//            String lastData = bitmapString;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//        return bitmapString;
//    }
    public static String getImageViewStr(ImageView imageView) {
//        String path = null;
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        } else {
//            path = Environment.getDownloadCacheDirectory().getAbsolutePath();
//        }
//        path = path + "/jkcq/fit/logo";
//        File file = new File(path);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        path = file.getAbsolutePath();
//        String pathTemp = path + "/1.JPEG";
        Bitmap btm = changeToBitMap(imageView);
        String bitmapString = convertIconToString(btm);
//        try {
//            BitmapUtils.saveBitmapFile(btm, pathTemp);
//            bitmapString = BitmapUtils.encodeBase64File(pathTemp);
//            String lastData = bitmapString;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
        return bitmapString;
    }


    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
//    public static Bitmap convertStringToIcon(String st) {
//        // OutputStream out;
//        Bitmap bitmap = null;
//        try {
//            // out = new FileOutputStream("/sdcard/aa.jpg");
//            byte[] bitmapArray;
//            bitmapArray = Base64.decode(st, Base64.DEFAULT);
//            bitmap =
//                    BitmapFactory.decodeByteArray(bitmapArray, 0,
//                            bitmapArray.length);
//            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//            return bitmap;
//        } catch (Exception e) {
//            return null;
//        }
//    }


    /**
     * 将imageView转换成Bitmap
     *
     * @param imageView
     * @return
     */
    public static Bitmap changeToBitMap(ImageView imageView) {
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        return image;
    }

    public static byte[] bmpToByteArray(Bitmap thumb, boolean is)
            throws Exception {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        thumb.compress(CompressFormat.JPEG, 10, bStream);
        byte[] bytes = bStream.toByteArray();
        bStream.close();
        return bytes;
    }


    private static final int BLACK = 0xff000000;

    /**
     * 基于质量的压缩算法， 此方法未 解决压缩后图像失真问题
     * <br> 可先调用比例压缩适当压缩图片后，再调用此方法可解决上述问题
     *
     * @param maxBytes 压缩后的图像最大大小 单位为byte
     * @return
     */
    public final static Bitmap compressBitmap(Bitmap bitmap, long maxBytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            bitmap.compress(CompressFormat.JPEG, options, baos);
            while (baos.toByteArray().length / 1024 > maxBytes && options > 0) {
                baos.reset();
                options -= 10;
                bitmap.compress(CompressFormat.JPEG, options, baos);

                Logger.e("compressBitmap length = " + baos.toByteArray().length);
            }
            byte[] bts = baos.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
            baos.close();
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * caculate the bitmap sampleSize
     *
     * @return
     */
    public final static int caculateInSampleSize(BitmapFactory.Options options, int rqsW, int rqsH) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (rqsW == 0 || rqsH == 0) return 1;
        if (height > rqsH || width > rqsW) {
            final int heightRatio = Math.round((float) height / (float) rqsH);
            final int widthRatio = Math.round((float) width / (float) rqsW);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;

            Logger.e("caculateInSampleSize inSampleSize = " + inSampleSize);
        }
        return inSampleSize;
    }

    static {
        System.loadLibrary("jpeg");// libjpeg
        System.loadLibrary("imagerar");// 我们自己的库
    }

    /**
     * 本地方法 JNI处理图片
     *
     * @param bitmap   bitmap
     * @param width    宽度
     * @param height   高度
     * @param quality  图片质量 100表示不变 越小就压缩越严重
     * @param fileName 文件路径的byte数组
     * @param optimize 是否采用哈弗曼表数据计算
     * @return "0"失败, "1"成功
     */
    public static native String compressBitmap(Bitmap bitmap, int width,
                                               int height, int quality, byte[] fileName, boolean optimize);

    /**
     * 尺寸和质量压缩 没有oom处理
     *
     * @param bitmap   bitmap
     * @param filePath 文件路径
     */
    public static void compressBitmap(Bitmap bitmap, String filePath, int maxSize) {
        // 根据设定的最大分辨率获取压缩比例
        int ratio = BitmapUtils.getRatioSize(bitmap.getWidth(),
                bitmap.getHeight());

        int afterWidth = bitmap.getWidth() / ratio;
        int afterHeight = bitmap.getHeight() / ratio;
        // 根据比例压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(afterWidth, afterHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, afterWidth, afterHeight);
        canvas.drawBitmap(bitmap, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        result.compress(CompressFormat.JPEG, options, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxSize) {
            // 重置baos
            baos.reset();
            options -= 10;
            result.compress(CompressFormat.JPEG, options, baos);
        }

        Log.e("BitmapUtils", "save image " + options);

        // 保存图片 true表示使用哈夫曼算法
        BitmapUtils.saveBitmap(result, options, filePath, true);

        if (result != null && !result.isRecycled()) {
            result.recycle();
            result = null;
        }
    }

    /**
     * 尺寸和质量压缩 没有oom处理
     *
     * @param compressFilepath 原始文件路径
     * @param filePath         目标文件路径
     */
    public static void compressBitmap(String compressFilepath, String filePath) {
        // 最大图片大小 150KB
        int maxSize = 150;
        // 根据地址获取bitmap
        Bitmap result = getBitmapFromFile(compressFilepath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int quality = 100;
        result.compress(CompressFormat.JPEG, quality, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxSize) {
            // 重置baos即清空baos
            baos.reset();
            quality -= 10;
            result.compress(CompressFormat.JPEG, quality, baos);
        }
        // 保存图片 true表示使用哈夫曼算法
        BitmapUtils.saveBitmap(result, quality, filePath, true);
        // 释放Bitmap
        if (!result.isRecycled()) {
            result.recycle();
        }
    }

    /**
     * 通过文件路径读获取Bitmap防止OOM以及解决图片旋转问题
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        BitmapFactory.decodeFile(filePath, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 获取尺寸压缩倍数
        newOpts.inSampleSize = BitmapUtils.getRatioSize(w, h);
        newOpts.inJustDecodeBounds = false;// 读取所有内容
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        File file = new File(filePath);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
                        newOpts);
                // 旋转图片
                int photoDegree = readPictureDegree(filePath);
                if (photoDegree != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(photoDegree);
                    // 创建新的图片
                    bitmap = Bitmap
                            .createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), matrix, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 读取旋转角度
     *
     * @param path 文件路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 计算缩放比
     *
     * @param bitWidth  图片宽度
     * @param bitHeight 图片高度
     * @return 比例
     */
    public static int getRatioSize(int bitWidth, int bitHeight) {
        // 图片最大分辨率
        int imageHeight = 1280;
        int imageWidth = 960;
        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1;
        return ratio;
    }

    /**
     * 经过java层压缩后再通过libjpeg库压缩
     *
     * @param bitmap   bitmap
     * @param quality  图片质量 100表示不变 越小就压缩越严重
     * @param fileName 文件路径的byte数组
     * @param optimize 是否采用哈弗曼表数据计算
     */
    private static void saveBitmap(Bitmap bitmap, int quality, String fileName,
                                   boolean optimize) {
        String code = compressBitmap(bitmap, bitmap.getWidth(),
                bitmap.getHeight(), quality, fileName.getBytes(), optimize);
        Log.e("BitmapUtils", "code " + code);
    }

    /**
     * 缩放图片
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        try {
            // 获得图片的宽高
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return newBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }



}
