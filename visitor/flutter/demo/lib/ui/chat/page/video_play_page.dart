import 'package:chewie/chewie.dart';
import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart';

class VideoPlayPage extends StatefulWidget {
  final String? videoUrl;
  final String? title;
  const VideoPlayPage({super.key, this.title = '视频播放', @required this.videoUrl});

  @override
  State<StatefulWidget> createState() {
    return _VideoPlayPageState();
  }
}

class _VideoPlayPageState extends State<VideoPlayPage> {
  //
  // TargetPlatform _platform;
  VideoPlayerController? _videoPlayerController1;
  ChewieController? _chewieController;
  String? _videoUrl;

  @override
  void initState() {
    _videoUrl = widget.videoUrl;
    super.initState();
    initializePlayer();
  }

  Future<void> initializePlayer() async {
    // _videoPlayerController1 = VideoPlayerController.network(_videoUrl!);
    _videoPlayerController1 = VideoPlayerController.networkUrl(Uri.parse(_videoUrl!));
    await _videoPlayerController1!.initialize();
    _chewieController = ChewieController(
      videoPlayerController: _videoPlayerController1!,
      autoPlay: true,
      // looping: false,
      // Try playing around with some of these other options:
      // showControls: true,
      // materialProgressColors: ChewieProgressColors(
      //   playedColor: Colors.red,
      //   handleColor: Colors.blue,
      //   backgroundColor: Colors.grey,
      //   bufferedColor: Colors.lightGreen,
      // ),
      // placeholder: Container(
      //   color: Colors.grey,
      // ),
      autoInitialize: true,
    );
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title!),
        centerTitle: true,
        elevation: 0,
      ),
      body: Column(
        children: <Widget>[
          Expanded(
            child: Center(
              child: _chewieController != null &&
                      _chewieController!
                          .videoPlayerController.value.isInitialized
                  ? Chewie(
                      controller: _chewieController!,
                    )
                  : const Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        CircularProgressIndicator(),
                        SizedBox(height: 20),
                        Text('Loading'),
                      ],
                    ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _videoPlayerController1!.dispose();
    _chewieController!.dispose();
    super.dispose();
  }
}
