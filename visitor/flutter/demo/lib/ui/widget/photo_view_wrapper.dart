import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:flutter/material.dart';
import 'package:photo_view/photo_view.dart';

// TODO: 保存到本地相册
class PhotoViewWrapper extends StatelessWidget {
  //
  const PhotoViewWrapper({super.key, 
    this.imageUrl,
    this.imageProvider,
    this.loadingBuilder,
    this.backgroundDecoration,
    this.minScale,
    this.maxScale,
    this.initialScale,
    this.basePosition = Alignment.center,
    this.filterQuality = FilterQuality.none,
    this.disableGestures,
  });

  final String? imageUrl;
  final ImageProvider? imageProvider;
  final LoadingBuilder? loadingBuilder;
  final BoxDecoration? backgroundDecoration;
  final dynamic minScale;
  final dynamic maxScale;
  final dynamic initialScale;
  final Alignment? basePosition;
  final FilterQuality? filterQuality;
  final bool? disableGestures;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
          constraints: BoxConstraints.expand(
            height: MediaQuery.of(context).size.height,
          ),
          child: Stack(
            alignment: Alignment.center,
            children: [
              GestureDetector(
                onTap: () {
                  Navigator.pop(context);
                },
                child: PhotoView(
                  imageProvider: imageProvider,
                  loadingBuilder: loadingBuilder,
                  backgroundDecoration: backgroundDecoration,
                  minScale: minScale,
                  maxScale: maxScale,
                  initialScale: initialScale,
                  basePosition: basePosition,
                  filterQuality: filterQuality,
                  disableGestures: disableGestures,
                ),
              ),
              Positioned(
                bottom: 100,
                child: InkWell(
                  onTap: () {
                    debugPrint('imageurl: $imageUrl');
                    BytedeskUtils.saveImage(imageUrl!);
                  },
                  child: const Text(
                    '保存到相册',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
              )
            ],
          )),
    );
  }
}
