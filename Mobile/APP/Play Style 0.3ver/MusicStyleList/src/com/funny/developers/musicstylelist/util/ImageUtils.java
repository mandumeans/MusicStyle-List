package com.funny.developers.musicstylelist.util;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageUtils {

	private static ImageLoader imageLoader;
	private static ImageLoaderConfiguration imageConfig;

	public static void initialImageLoader(Context context){
		imageLoader = ImageLoader.getInstance();
		imageConfig = new ImageLoaderConfiguration.Builder(context).build();
		imageLoader.init(imageConfig);
	}

	public static void destoryImageLoader(){
		if(imageLoader != null) {
			imageLoader.destroy();
		}
	}

	public static void displayUrlImage(ImageView imageview, String thumnailUrl, int defaultImageId){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(defaultImageId)
		.showImageForEmptyUri(defaultImageId)
		.showImageOnFail(defaultImageId)
		.cacheInMemory(true)
        .cacheOnDisk(true)
		.build();

		imageLoader.displayImage(thumnailUrl, imageview, options);
	}
}
