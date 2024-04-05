// ignore_for_file: constant_identifier_names

import '../src/category_emoji.dart';
import '../src/config.dart';
import '../src/default_emoji_picker_view.dart';
import '../src/emoji.dart';
import '../src/emoji_picker_internal_utils.dart';
import '../src/emoji_view_state.dart';
import '../src/recent_emoji.dart';
import 'package:flutter/material.dart';

/// All the possible categories that [Emoji] can be put into
///
/// All [Category] are shown in the category bar
enum Category {
  /// Recent emojis
  RECENT,

  /// Smiley emojis
  SMILEYS,

  /// Animal emojis
  ANIMALS,

  /// Food emojis
  FOODS,

  /// Activity emojis
  ACTIVITIES,

  /// Travel emojis
  TRAVEL,

  /// Ojects emojis
  OBJECTS,

  /// Sumbol emojis
  SYMBOLS,

  /// Flag emojis
  FLAGS,
}

/// Extension on Category enum to get its name
extension CategoryExtension on Category {
  /// Returns name of Category
  String get name {
    switch (this) {
      case Category.RECENT:
        return 'recent';
      case Category.SMILEYS:
        return 'smileys';
      case Category.ANIMALS:
        return 'animals';
      case Category.FOODS:
        return 'foods';
      case Category.ACTIVITIES:
        return 'activities';
      case Category.TRAVEL:
        return 'travel';
      case Category.OBJECTS:
        return 'objects';
      case Category.SYMBOLS:
        return 'symbols';
      case Category.FLAGS:
        return 'flags';
    }
  }
}

/// Enum to alter the keyboard button style
enum ButtonMode {
  /// Android button style - gives the button a splash color with ripple effect
  MATERIAL,

  /// iOS button style - gives the button a fade out effect when pressed
  CUPERTINO
}

/// Callback function for when emoji is selected
///
/// The function returns the selected [Emoji] as well
/// as the [Category] from which it originated
typedef OnEmojiSelected = void Function(Category category, Emoji emoji);

/// Callback function for backspace button
typedef OnBackspacePressed = void Function();

/// Callback function for custom view
typedef EmojiViewBuilder = Widget Function(Config config, EmojiViewState state);

/// The Emoji Keyboard widget
///
/// This widget displays a grid of [Emoji] sorted by [Category]
/// which the user can horizontally scroll through.
///
/// There is also a bottombar which displays all the possible [Category]
/// and allow the user to quickly switch to that [Category]
class EmojiPicker extends StatefulWidget {
  /// EmojiPicker for flutter
  const EmojiPicker({
    Key? key,
    this.textEditingController,
    this.onEmojiSelected,
    this.onBackspacePressed,
    this.config = const Config(),
    this.customWidget,
  }) : super(key: key);

  /// Custom widget
  final EmojiViewBuilder? customWidget;

  /// If you provide the [TextEditingController] that is linked to a
  /// [TextField] this widget handles inserting and deleting for you
  /// automatically.
  final TextEditingController? textEditingController;

  /// The function called when the emoji is selected
  final OnEmojiSelected? onEmojiSelected;

  /// The function called when backspace button is pressed
  final OnBackspacePressed? onBackspacePressed;

  /// Config for customizations
  final Config config;

  @override
  EmojiPickerState createState() => EmojiPickerState();
}

/// EmojiPickerState
class EmojiPickerState extends State<EmojiPicker> {
  final List<CategoryEmoji> _categoryEmoji = List.empty(growable: true);
  List<RecentEmoji> _recentEmoji = List.empty(growable: true);
  late Future<void> _updateEmojiFuture;

  // Prevent emojis to be reloaded with every build
  bool _loaded = false;

  // Internal helper
  final _emojiPickerInternalUtils = EmojiPickerInternalUtils();

  /// Update recentEmoji list from outside using EmojiPickerUtils
  void updateRecentEmoji(List<RecentEmoji> recentEmoji) {
    _recentEmoji = recentEmoji;
    if (mounted) {
      setState(() {});
    }
  }

  @override
  void initState() {
    super.initState();
    _updateEmojiFuture = _updateEmojis();
  }

  @override
  void didUpdateWidget(covariant EmojiPicker oldWidget) {
    if (oldWidget.config != widget.config) {
      // Config changed - rebuild EmojiPickerView completely
      _loaded = false;
      _updateEmojiFuture = _updateEmojis();
    }
    super.didUpdateWidget(oldWidget);
  }

  @override
  Widget build(BuildContext context) {
    if (!_loaded) {
      // Load emojis
      _updateEmojiFuture.then(
        (value) => WidgetsBinding.instance.addPostFrameCallback((_) {
          if (!mounted) return;
          setState(() {
            _loaded = true;
          });
        }),
      );

      // Show loading indicator
      return Container(
        alignment: Alignment.center,
        color: widget.config.bgColor,
        child: const CircularProgressIndicator(),
      );
    }
    if (widget.config.showRecentsTab) {
      _categoryEmoji[0].emoji = _recentEmoji.map((e) => e.emoji).toList();
    }

    var state = EmojiViewState(
      _categoryEmoji,
      _getOnEmojiListener(),
      widget.onBackspacePressed == null && widget.textEditingController == null
          ? null
          : _onBackspacePressed,
    );

    // Build
    return widget.customWidget == null
        ? DefaultEmojiPickerView(widget.config, state)
        : widget.customWidget!(widget.config, state);
  }

  void _onBackspacePressed() {
    if (widget.textEditingController != null) {
      final controller = widget.textEditingController!;

      final selection = controller.value.selection;
      final text = controller.value.text;
      final cursorPosition = controller.selection.base.offset;

      if (cursorPosition < 0) {
        widget.onBackspacePressed?.call();
        return;
      }

      final newTextBeforeCursor =
          selection.textBefore(text).characters.skipLast(1).toString();
      controller
        ..text = newTextBeforeCursor + selection.textAfter(text)
        ..selection = TextSelection.fromPosition(
            TextPosition(offset: newTextBeforeCursor.length));
    }

    widget.onBackspacePressed?.call();
  }

  // Add recent emoji handling to tap listener
  OnEmojiSelected _getOnEmojiListener() {
    return (category, emoji) {
      if (widget.config.showRecentsTab) {
        _emojiPickerInternalUtils
            .addEmojiToRecentlyUsed(emoji: emoji, config: widget.config)
            .then((newRecentEmoji) => {
                  _recentEmoji = newRecentEmoji,
                  if (category != Category.RECENT && mounted)
                    setState(() {
                      // rebuild to update recent emoji tab
                      // when it is not current tab
                    })
                });
      }

      if (widget.textEditingController != null) {
        // based on https://stackoverflow.com/a/60058972/10975692
        final controller = widget.textEditingController!;
        final text = controller.text;
        final selection = controller.selection;
        final cursorPosition = controller.selection.base.offset;

        if (cursorPosition < 0) {
          controller.text += emoji.emoji;
          widget.onEmojiSelected?.call(category, emoji);
          return;
        }

        final newText =
            text.replaceRange(selection.start, selection.end, emoji.emoji);
        final emojiLength = emoji.emoji.length;
        controller
          ..text = newText
          ..selection = selection.copyWith(
            baseOffset: selection.start + emojiLength,
            extentOffset: selection.start + emojiLength,
          );
      }

      widget.onEmojiSelected?.call(category, emoji);
    };
  }

  // Initialize emoji data
  Future<void> _updateEmojis() async {
    _categoryEmoji.clear();
    if (widget.config.showRecentsTab) {
      _recentEmoji = await _emojiPickerInternalUtils.getRecentEmojis();
      final recentEmojiMap = _recentEmoji.map((e) => e.emoji).toList();
      _categoryEmoji.add(CategoryEmoji(Category.RECENT, recentEmojiMap));
    }

    final availableCategoryEmoji =
        await _emojiPickerInternalUtils.getAvailableCategoryEmoji();

    availableCategoryEmoji.forEach((category, emojis) async {
      _categoryEmoji.add(
        CategoryEmoji(
            category,
            emojis.entries.map((emojiMap) {
              var emoji = Emoji(emojiMap.key, emojiMap.value);
              // Emoji with skin tone are only in SMILEY & ACTIVITIES category
              if (category == Category.SMILEYS ||
                  category == Category.ACTIVITIES) {
                return _updateSkinToneSupport(emoji);
              } else {
                return emoji;
              }
            }).toList()),
      );
    });

    // Update emoji list version once all categories were cached
    _emojiPickerInternalUtils.updateEmojiVersion();
  }

  // Set [hasSkinTone] to true for emoji that support skin tones
  Emoji _updateSkinToneSupport(Emoji emoji) {
    if (_emojiPickerInternalUtils.hasSkinTone(emoji)) {
      return emoji.copyWith(hasSkinTone: true);
    }
    return emoji;
  }
}
