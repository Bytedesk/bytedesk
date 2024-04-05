import 'package:flutter/material.dart';

/// A class to store data for each individual emoji
@immutable
class Emoji {
  /// Emoji constructor
  const Emoji(this.name, this.emoji, {this.hasSkinTone = false});

  /// The name or description for this emoji
  final String name;

  /// The unicode string for this emoji
  ///
  /// This is the string that should be displayed to view the emoji
  final String emoji;

  /// Flag if emoji supports multiple skin tones
  final bool hasSkinTone;

  @override
  String toString() {
    return 'Name: $name, Emoji: $emoji, HasSkinTone: $hasSkinTone';
  }

  /// Parse Emoji from json
  static Emoji fromJson(Map<String, dynamic> json) {
    return Emoji(
      json['name'] as String,
      json['emoji'] as String,
      hasSkinTone:
          json['hasSkinTone'] != null ? json['hasSkinTone'] as bool : false,
    );
  }

  ///  Encode Emoji to json
  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'emoji': emoji,
      'hasSkinTone': hasSkinTone,
    };
  }

  /// Copy method
  Emoji copyWith({String? name, String? emoji, bool? hasSkinTone}) {
    return Emoji(
      name ?? this.name,
      emoji ?? this.emoji,
      hasSkinTone: hasSkinTone ?? this.hasSkinTone,
    );
  }
}
