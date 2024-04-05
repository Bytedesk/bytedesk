// import 'package:emoji_picker_flutter/emoji_picker_flutter.dart';
// import 'package:emoji_picker_flutter/src/category_emoji.dart';
// import 'package:emoji_picker_flutter/src/config.dart';
// import 'package:emoji_picker_flutter/src/emoji_picker_builder.dart';
// import 'package:emoji_picker_flutter/src/emoji_view_state.dart';
// ignore_for_file: library_private_types_in_public_api

import './emoji_picker_flutter/emoji_picker_flutter.dart';
import './emoji_picker_flutter/src/category_emoji.dart';
// import 'package:bytedesk_kefu/vendors/emoji_picker_flutter/src/config.dart';
// import 'package:bytedesk_kefu/vendors/emoji_picker_flutter/src/emoji_picker_builder.dart';
// import 'package:bytedesk_kefu/vendors/emoji_picker_flutter/src/emoji_view_state.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

/// EmojiPicker Implementation
class EmojiPickerView extends EmojiPickerBuilder {
  /// Constructor
  const EmojiPickerView(
    Config config,
    EmojiViewState state,
    this.handleSendPressed, {super.key}
  ) : super(config, state);

  @override
  _EmojiPickerViewState createState() => _EmojiPickerViewState();

  /// See [AttachmentButton.onPressed]
  final void Function()? handleSendPressed;
}

class _EmojiPickerViewState extends State<EmojiPickerView>
    with SingleTickerProviderStateMixin {
  PageController? _pageController;
  TabController? _tabController;

  @override
  void initState() {
    var initCategory = widget.state.categoryEmoji.indexWhere(
        (element) => element.category == widget.config.initCategory);
    if (initCategory == -1) {
      initCategory = 0;
    }
    _tabController = TabController(
        initialIndex: initCategory,
        length: widget.state.categoryEmoji.length,
        vsync: this);
    _pageController = PageController(initialPage: initCategory);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final emojiSize = widget.config.getEmojiSize(constraints.maxWidth);

        return Container(
          color: const Color.fromRGBO(251, 251, 251, 1.0), //widget.config.bgColor,
          child: Column(
            children: [
              Row(
                children: [
                  Expanded(
                    child: TabBar(
                      labelColor: widget.config.iconColorSelected,
                      indicatorColor: widget.config.indicatorColor,
                      unselectedLabelColor: widget.config.iconColor,
                      controller: _tabController,
                      labelPadding: EdgeInsets.zero,
                      onTap: (index) {
                        _pageController!.jumpToPage(index);
                      },
                      tabs: widget.state.categoryEmoji
                          .asMap()
                          .entries
                          .map<Widget>((item) =>
                              _buildCategory(item.key, item.value.category))
                          .toList(),
                    ),
                  ),
                  IconButton(
                    padding: const EdgeInsets.only(bottom: 2),
                    icon: Icon(
                      Icons.backspace,
                      color: widget.config.backspaceColor,
                    ),
                    onPressed: () {
                      widget.state.onBackspacePressed!();
                    },
                  ),
                  // IconButton(
                  //   // iconSize: 16,
                  //   onPressed: () {
                  //     widget.handleSendPressed!();
                  //   },
                  //   // backgroundColor: Colors.lightGreen,
                  //   icon: Icon(Icons.send),
                  // ),
                ],
              ),
              Flexible(
                child: PageView.builder(
                  itemCount: widget.state.categoryEmoji.length,
                  controller: _pageController,
                  onPageChanged: (index) {
                    _tabController!.animateTo(
                      index,
                      duration: widget.config.tabIndicatorAnimDuration,
                    );
                  },
                  itemBuilder: (context, index) =>
                      _buildPage(emojiSize, widget.state.categoryEmoji[index]),
                ),
              ),
              const Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [],
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildCategory(int index, Category category) {
    return Tab(
      icon: Icon(
        widget.config.getIconForCategory(category),
      ),
    );
  }

  Widget _buildButtonWidget(
      {required VoidCallback onPressed, required Widget child}) {
    if (widget.config.buttonMode == ButtonMode.MATERIAL) {
      return TextButton(
        onPressed: onPressed,
        style: ButtonStyle(padding: MaterialStateProperty.all(EdgeInsets.zero)),
        child: child,
      );
    }
    return CupertinoButton(
        padding: EdgeInsets.zero, onPressed: onPressed, child: child);
  }

  Widget _buildPage(double emojiSize, CategoryEmoji categoryEmoji) {
    // Display notice if recent has no entries yet
    if (categoryEmoji.category == Category.RECENT &&
        categoryEmoji.emoji.isEmpty) {
      return _buildNoRecent();
    }
    // Build page normally
    return GridView.count(
      scrollDirection: Axis.vertical,
      physics: const ScrollPhysics(),
      shrinkWrap: true,
      primary: true,
      padding: const EdgeInsets.all(0),
      crossAxisCount: widget.config.columns,
      mainAxisSpacing: widget.config.verticalSpacing,
      crossAxisSpacing: widget.config.horizontalSpacing,
      children: categoryEmoji.emoji
          .map<Widget>((item) => _buildEmoji(emojiSize, categoryEmoji, item))
          .toList(),
    );
  }

  Widget _buildEmoji(
    double emojiSize,
    CategoryEmoji categoryEmoji,
    Emoji emoji,
  ) {
    return _buildButtonWidget(
        onPressed: () {
          widget.state.onEmojiSelected(categoryEmoji.category, emoji);
        },
        child: FittedBox(
          fit: BoxFit.fill,
          child: Text(
            emoji.emoji,
            textScaleFactor: 1.0,
            style: TextStyle(
              fontSize: emojiSize,
              backgroundColor: Colors.transparent,
            ),
          ),
        ));
  }

  Widget _buildNoRecent() {
    return const Center(
        child: Text( '无最近使用表情',
      // widget.config.noRecentsText,
      // style: widget.config.noRecentsStyle,
      textAlign: TextAlign.center,
    ));
  }
}
