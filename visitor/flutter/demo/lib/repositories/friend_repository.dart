import 'package:bytedesk_kefu/http/bytedesk_friend_api.dart';
import 'package:bytedesk_kefu/model/friend.dart';

class FriendRepository {
  final BytedeskFriendHttpApi bytedeskHttpApi = BytedeskFriendHttpApi();

  Future<List<Friend>> getFriends(int? page, int? size) async {
    return await bytedeskHttpApi.getFriends(page, size);
  }

  Future<List<Friend>> getFriendsAddress(int? page, int? size) async {
    return await bytedeskHttpApi.getFriendsAddress(page, size);
  }

  Future<Friend> uploadAddress(String? nickname, String? mobile) async {
    return await bytedeskHttpApi.uploadAddress(nickname, mobile);
  }

  Future<List<Friend>> getFriendsNearby(int? page, int? size) async {
    return await bytedeskHttpApi.getFriendsNearby(page, size);
  }

  Future<void> updateLocation(double? latitude, double? longtitude) async {
    return await bytedeskHttpApi.updateLocation(latitude, longtitude);
  }
}
