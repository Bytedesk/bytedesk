import 'package:bytedesk_kefu/model/helpArticle.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:equatable/equatable.dart';

abstract class HelpState extends Equatable {
  const HelpState();

  @override
  List<Object> get props => [];
}

/// UnInitialized
class UnHelpState extends HelpState {
  const UnHelpState();

  @override
  String toString() => 'UnHelpState';
}

class HelpEmpty extends HelpState {
  @override
  String toString() => 'HelpEmpty';
}

class HelpLoading extends HelpState {
  @override
  String toString() => 'HelpLoading';
}

class HelpLoadError extends HelpState {
  @override
  String toString() => 'HelpLoadError';
}

/// Initialized
class GetCategorySuccess extends HelpState {
  final List<HelpCategory> categoryList;

  const GetCategorySuccess(this.categoryList) : super();

  @override
  String toString() => 'GetCategorySuccess';
}

class GetArticleSuccess extends HelpState {
  final List<HelpArticle> articleList;

  const GetArticleSuccess(this.articleList) : super();

  @override
  String toString() => 'GetArticleSuccess';
}
