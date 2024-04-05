// ignore_for_file: constant_identifier_names

import 'dart:math';

import '../src/category_icons.dart';
import '../src/emoji_picker.dart';
import 'package:flutter/material.dart';

/// Default Widget if no recent is available
const DefaultNoRecentsWidget = Text(
  'No Recents',
  style: TextStyle(fontSize: 20, color: Colors.black26),
  textAlign: TextAlign.center,
);

/// Config for customizations
class Config {
  /// Constructor
  const Config(
      {this.columns = 7,
      this.emojiSizeMax = 32.0,
      this.verticalSpacing = 0,
      this.horizontalSpacing = 0,
      this.gridPadding = EdgeInsets.zero,
      this.initCategory = Category.RECENT,
      this.bgColor = const Color(0xFFEBEFF2),
      this.indicatorColor = Colors.blue,
      this.iconColor = Colors.grey,
      this.iconColorSelected = Colors.blue,
      this.progressIndicatorColor = Colors.blue,
      this.backspaceColor = Colors.blue,
      this.skinToneDialogBgColor = Colors.white,
      this.skinToneIndicatorColor = Colors.grey,
      this.enableSkinTones = true,
      this.showRecentsTab = true,
      this.recentsLimit = 28,
      this.replaceEmojiOnLimitExceed = false,
      this.noRecents = DefaultNoRecentsWidget,
      this.tabIndicatorAnimDuration = kTabScrollDuration,
      this.categoryIcons = const CategoryIcons(),
      this.buttonMode = ButtonMode.MATERIAL});

  /// Number of emojis per row
  final int columns;

  /// Width and height the emoji will be maximal displayed
  /// Can be smaller due to screen size and amount of columns
  final double emojiSizeMax;

  /// Verical spacing between emojis
  final double verticalSpacing;

  /// Horizontal spacing between emojis
  final double horizontalSpacing;

  /// The initial [Category] that will be selected
  /// This [Category] will have its button in the bottombar darkened
  final Category initCategory;

  /// The background color of the Widget
  final Color bgColor;

  /// The color of the category indicator
  final Color indicatorColor;

  /// The color of the category icons
  final Color iconColor;

  /// The color of the category icon when selected
  final Color iconColorSelected;

  /// The color of the loading indicator during initalization
  final Color progressIndicatorColor;

  /// The color of the backspace icon button
  final Color backspaceColor;

  /// The background color of the skin tone dialog
  final Color skinToneDialogBgColor;

  /// Color of the small triangle next to multiple skin tone emoji
  final Color skinToneIndicatorColor;

  /// Enable feature to select a skin tone of certain emoji's
  final bool enableSkinTones;

  /// Show extra tab with recently used emoji
  final bool showRecentsTab;

  /// Limit of recently used emoji that will be saved
  final int recentsLimit;

  /// A widget (usually [Text]) to be displayed if no recent emojis to display
  final Widget noRecents;

  /// Duration of tab indicator to animate to next category
  final Duration tabIndicatorAnimDuration;

  /// Determines the icon to display for each [Category]
  final CategoryIcons categoryIcons;

  /// Change between Material and Cupertino button style
  final ButtonMode buttonMode;

  /// The padding of GridView, default is [EdgeInsets.zero]
  final EdgeInsets gridPadding;

  /// Replace latest emoji on recents list on limit exceed
  final bool replaceEmojiOnLimitExceed;

  /// Get Emoji size based on properties and screen width
  double getEmojiSize(double width) {
    final maxSize = width / columns;
    return min(maxSize, emojiSizeMax);
  }

  /// Returns the icon for the category
  IconData getIconForCategory(Category category) {
    switch (category) {
      case Category.RECENT:
        return categoryIcons.recentIcon;
      case Category.SMILEYS:
        return categoryIcons.smileyIcon;
      case Category.ANIMALS:
        return categoryIcons.animalIcon;
      case Category.FOODS:
        return categoryIcons.foodIcon;
      case Category.TRAVEL:
        return categoryIcons.travelIcon;
      case Category.ACTIVITIES:
        return categoryIcons.activityIcon;
      case Category.OBJECTS:
        return categoryIcons.objectIcon;
      case Category.SYMBOLS:
        return categoryIcons.symbolIcon;
      case Category.FLAGS:
        return categoryIcons.flagIcon;
      default:
        throw Exception('Unsupported Category');
    }
  }

  @override
  bool operator ==(other) {
    return (other is Config) &&
        other.columns == columns &&
        other.emojiSizeMax == emojiSizeMax &&
        other.verticalSpacing == verticalSpacing &&
        other.horizontalSpacing == horizontalSpacing &&
        other.initCategory == initCategory &&
        other.bgColor == bgColor &&
        other.indicatorColor == indicatorColor &&
        other.iconColor == iconColor &&
        other.iconColorSelected == iconColorSelected &&
        other.progressIndicatorColor == progressIndicatorColor &&
        other.backspaceColor == backspaceColor &&
        other.skinToneDialogBgColor == skinToneDialogBgColor &&
        other.skinToneIndicatorColor == skinToneIndicatorColor &&
        other.enableSkinTones == enableSkinTones &&
        other.showRecentsTab == showRecentsTab &&
        other.recentsLimit == recentsLimit &&
        other.noRecents == noRecents &&
        other.tabIndicatorAnimDuration == tabIndicatorAnimDuration &&
        other.categoryIcons == categoryIcons &&
        other.buttonMode == buttonMode &&
        other.gridPadding == gridPadding &&
        other.replaceEmojiOnLimitExceed == replaceEmojiOnLimitExceed;
  }

  @override
  int get hashCode =>
      columns.hashCode ^
      emojiSizeMax.hashCode ^
      verticalSpacing.hashCode ^
      horizontalSpacing.hashCode ^
      initCategory.hashCode ^
      bgColor.hashCode ^
      indicatorColor.hashCode ^
      iconColor.hashCode ^
      iconColorSelected.hashCode ^
      progressIndicatorColor.hashCode ^
      backspaceColor.hashCode ^
      skinToneDialogBgColor.hashCode ^
      skinToneIndicatorColor.hashCode ^
      enableSkinTones.hashCode ^
      showRecentsTab.hashCode ^
      recentsLimit.hashCode ^
      noRecents.hashCode ^
      tabIndicatorAnimDuration.hashCode ^
      categoryIcons.hashCode ^
      buttonMode.hashCode ^
      gridPadding.hashCode ^
      replaceEmojiOnLimitExceed.hashCode;
}
