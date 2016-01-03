# Advanced_CircleBitmapDisplayer
This is a displayer implementation for the Android Universal Image Loader  https://github.com/nostra13/Android-Universal-Image-Loader.
With this displayer you can make the image loader displays cricle image with fade-in animation and border. This class is built on top of 
version 1.9.5 of the universal image loader

### Usage
In an android Activity/Fragment:

    ImageView imageView = ...// the image view you want to use
    Context context = ... // an android context

    DisplayImageOptions defaultOptions =
        new DisplayImageOptions.Builder().
        displayer(new AdvancedCircleBitmapDisplayer(300, 5, 0xffffffff)) // with fade-in and white 5-pixel thick border 
        .build();
        
    ImageLoaderConfiguration config = 
      new ImageLoaderConfiguration
      .Builder(context)
      .defaultDisplayImageOptions(defaultOptions)
      .build();
      
    ImageLoader imageLoader = ImageLoader.getInstance();
    imageLoader.init(config);
    imageLoader.displayImage("your image url", imageView);
