import 'dart:math' as math;

import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:flutter/material.dart';

class ItemModel {
  String name;
  String title;
  IconData icon;

  ItemModel(this.name, this.title, this.icon);
}

enum PressType {
  longPress,
  singleClick,
}

enum PreferredPosition {
  top,
  bottom,
}

class BytedeskPopupMenuController extends ChangeNotifier {
  bool menuIsShowing = false;

  void showMenu() {
    menuIsShowing = true;
    notifyListeners();
  }

  void hideMenu() {
    menuIsShowing = false;
    notifyListeners();
  }

  void toggleMenu() {
    menuIsShowing = !menuIsShowing;
    notifyListeners();
  }
}

Rect _menuRect = Rect.zero;

class BytedeskPopupMenu extends StatefulWidget {
  const BytedeskPopupMenu({
    super.key,
    required this.child,
    required this.menuBuilder,
    required this.pressType,
    this.controller,
    this.arrowColor = const Color(0xFF4C4C4C),
    this.showArrow = true,
    this.barrierColor = Colors.black12,
    this.arrowSize = 10.0,
    this.horizontalMargin = 10.0,
    this.verticalMargin = 10.0,
    this.position,
    this.menuOnChange,
    this.enablePassEvent = true,
  });

  final Widget child;
  final PressType pressType;
  final bool showArrow;
  final Color arrowColor;
  final Color barrierColor;
  final double horizontalMargin;
  final double verticalMargin;
  final double arrowSize;
  final BytedeskPopupMenuController? controller;
  // final Widget Function() menuBuilder;
  final Widget menuBuilder;
  final PreferredPosition? position;
  final void Function(bool)? menuOnChange;

  /// Pass tap event to the widgets below the mask.
  /// It only works when [barrierColor] is transparent.
  final bool enablePassEvent;

  @override
  State<BytedeskPopupMenu> createState() => _BytedeskPopupMenuState();
}

class _BytedeskPopupMenuState extends State<BytedeskPopupMenu> {
  RenderBox? _childBox;
  RenderBox? _parentBox;
  OverlayEntry? _overlayEntry;
  BytedeskPopupMenuController? _controller;
  bool _canResponse = true;

  _showMenu() {
    Widget arrow = ClipPath(
      clipper: _ArrowClipper(),
      child: Container(
        width: widget.arrowSize,
        height: widget.arrowSize,
        color: widget.arrowColor,
      ),
    );

    _overlayEntry = OverlayEntry(
      builder: (context) {
        Widget menu = Center(
          child: Container(
            constraints: BoxConstraints(
              maxWidth: _parentBox!.size.width - 2 * widget.horizontalMargin,
              minWidth: 0,
            ),
            child: CustomMultiChildLayout(
              delegate: _MenuLayoutDelegate(
                anchorSize: _childBox!.size,
                anchorOffset: _childBox!.localToGlobal(
                  Offset(-widget.horizontalMargin, 0),
                ),
                verticalMargin: widget.verticalMargin,
                position: widget.position,
              ),
              children: <Widget>[
                if (widget.showArrow)
                  LayoutId(
                    id: _MenuLayoutId.arrow,
                    child: arrow,
                  ),
                if (widget.showArrow)
                  LayoutId(
                    id: _MenuLayoutId.downArrow,
                    child: Transform.rotate(
                      angle: math.pi,
                      child: arrow,
                    ),
                  ),
                LayoutId(
                  id: _MenuLayoutId.content,
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: <Widget>[
                      Material(
                        color: Colors.transparent,
                        // child: widget.menuBuilder(),
                        child: widget.menuBuilder,
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
        return Listener(
          behavior: widget.enablePassEvent
              ? HitTestBehavior.translucent
              : HitTestBehavior.opaque,
          onPointerDown: (PointerDownEvent event) {
            Offset offset = event.localPosition;
            // If tap position in menu
            if (_menuRect.contains(
                Offset(offset.dx - widget.horizontalMargin, offset.dy))) {
              return;
            }
            _controller?.hideMenu();
            // When [enablePassEvent] works and we tap the [child] to [hideMenu],
            // but the passed event would trigger [showMenu] again.
            // So, we use time threshold to solve this bug.
            _canResponse = false;
            Future.delayed(const Duration(milliseconds: 300))
                .then((_) => _canResponse = true);
          },
          child: widget.barrierColor == Colors.transparent
              ? menu
              : Container(
                  color: widget.barrierColor,
                  child: menu,
                ),
        );
      },
    );
    if (_overlayEntry != null) {
      Overlay.of(context).insert(_overlayEntry!);
    }
  }

  _hideMenu() {
    if (_overlayEntry != null) {
      _overlayEntry?.remove();
      _overlayEntry = null;
    }
  }

  _updateView() {
    bool menuIsShowing = _controller?.menuIsShowing ?? false;
    widget.menuOnChange?.call(menuIsShowing);
    if (menuIsShowing) {
      _showMenu();
    } else {
      _hideMenu();
    }
  }

  @override
  void initState() {
    super.initState();
    _controller = widget.controller;
    _controller ??= BytedeskPopupMenuController();
    _controller?.addListener(_updateView);
    WidgetsBinding.instance.addPostFrameCallback((call) {
      if (mounted) {
        _childBox = context.findRenderObject() as RenderBox?;
        _parentBox =
            Overlay.of(context).context.findRenderObject() as RenderBox?;
      }
    });
  }

  @override
  void dispose() {
    _hideMenu();
    _controller?.removeListener(_updateView);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var child = Material(
      color: Colors.transparent,
      child: InkWell(
        hoverColor: Colors.transparent,
        focusColor: Colors.transparent,
        splashColor: Colors.transparent,
        highlightColor: Colors.transparent,
        child: widget.child,
        onTap: () {
          if (widget.pressType == PressType.singleClick && _canResponse) {
            _controller?.showMenu();
          }
        },
        onLongPress: () {
          if (widget.pressType == PressType.longPress && _canResponse) {
            _controller?.showMenu();
          }
        },
      ),
    );
    if (BytedeskUtils.isIOS) {
      return child;
    } else {
      return WillPopScope(
        onWillPop: () {
          _hideMenu();
          return Future.value(true);
        },
        child: child,
      );
    }
  }
}

enum _MenuLayoutId {
  arrow,
  downArrow,
  content,
}

enum _MenuPosition {
  bottomLeft,
  bottomCenter,
  bottomRight,
  topLeft,
  topCenter,
  topRight,
}

class _MenuLayoutDelegate extends MultiChildLayoutDelegate {
  _MenuLayoutDelegate({
    required this.anchorSize,
    required this.anchorOffset,
    required this.verticalMargin,
    this.position,
  });

  final Size anchorSize;
  final Offset anchorOffset;
  final double verticalMargin;
  final PreferredPosition? position;

  @override
  void performLayout(Size size) {
    Size contentSize = Size.zero;
    Size arrowSize = Size.zero;
    Offset contentOffset = const Offset(0, 0);
    Offset arrowOffset = const Offset(0, 0);

    double anchorCenterX = anchorOffset.dx + anchorSize.width / 2;
    double anchorTopY = anchorOffset.dy;
    double anchorBottomY = anchorTopY + anchorSize.height;
    _MenuPosition menuPosition = _MenuPosition.bottomCenter;

    if (hasChild(_MenuLayoutId.content)) {
      contentSize = layoutChild(
        _MenuLayoutId.content,
        BoxConstraints.loose(size),
      );
    }
    if (hasChild(_MenuLayoutId.arrow)) {
      arrowSize = layoutChild(
        _MenuLayoutId.arrow,
        BoxConstraints.loose(size),
      );
    }
    if (hasChild(_MenuLayoutId.downArrow)) {
      layoutChild(
        _MenuLayoutId.downArrow,
        BoxConstraints.loose(size),
      );
    }

    bool isTop = false;
    if (position == null) {
      // auto calculate position
      isTop = anchorBottomY > size.height / 2;
    } else {
      isTop = position == PreferredPosition.top;
    }
    if (anchorCenterX - contentSize.width / 2 < 0) {
      menuPosition = isTop ? _MenuPosition.topLeft : _MenuPosition.bottomLeft;
    } else if (anchorCenterX + contentSize.width / 2 > size.width) {
      menuPosition = isTop ? _MenuPosition.topRight : _MenuPosition.bottomRight;
    } else {
      menuPosition =
          isTop ? _MenuPosition.topCenter : _MenuPosition.bottomCenter;
    }

    switch (menuPosition) {
      case _MenuPosition.bottomCenter:
        arrowOffset = Offset(
          anchorCenterX - arrowSize.width / 2,
          anchorBottomY + verticalMargin,
        );
        contentOffset = Offset(
          anchorCenterX - contentSize.width / 2,
          anchorBottomY + verticalMargin + arrowSize.height,
        );
        break;
      case _MenuPosition.bottomLeft:
        arrowOffset = Offset(anchorCenterX - arrowSize.width / 2,
            anchorBottomY + verticalMargin);
        contentOffset = Offset(
          0,
          anchorBottomY + verticalMargin + arrowSize.height,
        );
        break;
      case _MenuPosition.bottomRight:
        arrowOffset = Offset(anchorCenterX - arrowSize.width / 2,
            anchorBottomY + verticalMargin);
        contentOffset = Offset(
          size.width - contentSize.width,
          anchorBottomY + verticalMargin + arrowSize.height,
        );
        break;
      case _MenuPosition.topCenter:
        arrowOffset = Offset(
          anchorCenterX - arrowSize.width / 2,
          anchorTopY - verticalMargin - arrowSize.height,
        );
        contentOffset = Offset(
          anchorCenterX - contentSize.width / 2,
          anchorTopY - verticalMargin - arrowSize.height - contentSize.height,
        );
        break;
      case _MenuPosition.topLeft:
        arrowOffset = Offset(
          anchorCenterX - arrowSize.width / 2,
          anchorTopY - verticalMargin - arrowSize.height,
        );
        contentOffset = Offset(
          0,
          anchorTopY - verticalMargin - arrowSize.height - contentSize.height,
        );
        break;
      case _MenuPosition.topRight:
        arrowOffset = Offset(
          anchorCenterX - arrowSize.width / 2,
          anchorTopY - verticalMargin - arrowSize.height,
        );
        contentOffset = Offset(
          size.width - contentSize.width,
          anchorTopY - verticalMargin - arrowSize.height - contentSize.height,
        );
        break;
    }
    if (hasChild(_MenuLayoutId.content)) {
      positionChild(_MenuLayoutId.content, contentOffset);
    }

    _menuRect = Rect.fromLTWH(
      contentOffset.dx,
      contentOffset.dy,
      contentSize.width,
      contentSize.height,
    );
    bool isBottom = false;
    if (_MenuPosition.values.indexOf(menuPosition) < 3) {
      // bottom
      isBottom = true;
    }
    if (hasChild(_MenuLayoutId.arrow)) {
      positionChild(
        _MenuLayoutId.arrow,
        isBottom
            ? Offset(arrowOffset.dx, arrowOffset.dy + 0.1)
            : const Offset(-100, 0),
      );
    }
    if (hasChild(_MenuLayoutId.downArrow)) {
      positionChild(
        _MenuLayoutId.downArrow,
        !isBottom
            ? Offset(arrowOffset.dx, arrowOffset.dy - 0.1)
            : const Offset(-100, 0),
      );
    }
  }

  @override
  bool shouldRelayout(MultiChildLayoutDelegate oldDelegate) => false;
}

class _ArrowClipper extends CustomClipper<Path> {
  @override
  Path getClip(Size size) {
    Path path = Path();
    path.moveTo(0, size.height);
    path.lineTo(size.width / 2, size.height / 2);
    path.lineTo(size.width, size.height);
    return path;
  }

  @override
  bool shouldReclip(CustomClipper<Path> oldClipper) {
    return true;
  }
}
