// ignore_for_file: file_names

import 'dart:async';

// import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:bytedesk_kefu/model/model.dart';

// https://pub.dev/packages/sqflite
class ThreadProvider {
  //
  static final ThreadProvider _singleton = ThreadProvider._internal();
  factory ThreadProvider() {
    return _singleton;
  }

  ThreadProvider._internal() {
    open();
  }

  //
  final String? tableThread = 'threads';
  final String? columnId = '_id';
  final String? columnTid = 'tid';
  final String? columnTopic = 'topic';
  final String? columnWid = 'wid';
  final String? columnUid = 'uid';
  final String? columnNickname = 'nickname';
  final String? columnAvatar = 'avatar';
  final String? columnContent = 'content';
  final String? columnTimestamp = 'timestamp';
  final String? columnUnreadCount = 'unreadCount';
  final String? columnType = 'type';
  final String? columnClient = 'client';
  final String? columnCurrentUid = 'currentUid';
  //
  Database? database;

  Future open() async {
    // Open the database and store the reference.
    database = await openDatabase(
      // Set the path to the database. Note: Using the `join` function from the
      // `path` package is best practice to ensure the path is correctly
      // constructed for each platform.
      join(await getDatabasesPath(), 'bytedesk-thread-v1.db'),
      // When the database is first created, create a table to store dogs.
      onCreate: (db, version) {
        // Run the CREATE TABLE statement on the database.
        return db.execute(
          "CREATE TABLE $tableThread($columnId INTEGER PRIMARY KEY autoincrement, $columnTid TEXT, $columnTopic TEXT, $columnWid TEXT, $columnUid TEXT, $columnNickname TEXT, $columnAvatar TEXT, $columnContent TEXT, $columnTimestamp TEXT, $columnUnreadCount integer, $columnType TEXT, $columnClient TEXT,  $columnCurrentUid TEXT)",
        );
      },
      // Set the version. This executes the onCreate function and provides a
      // path to perform database upgrades and downgrades.
      version: 1,
    );
  }

  Future<int> insert(Thread thread) async {
    // debugPrint('insert avatar:' + message.avatar + ' conten:' + message.content + ' timestamp:' + message.timestamp);
    return await database!.insert(tableThread!, thread.toMap());
  }

  //
  Future<List<Thread>> getThreads(String? currentUid) async {
    //
    List<Map> maps = await database!.query(
      tableThread!,
      columns: [
        columnTid!,
        columnTopic!,
        columnWid!,
        columnUid!,
        columnNickname!,
        columnAvatar!,
        columnContent!,
        columnTimestamp!,
        columnType!,
        columnUnreadCount!,
        columnClient!,
      ],
      where: '$columnCurrentUid = ?',
      whereArgs: [currentUid],
      orderBy: '$columnTimestamp DESC',
      // limit: size,
      // offset: page * size
    );
    //
    return List.generate(maps.length, (i) {
      //
      return Thread(
          tid: maps[i]['tid'],
          topic: maps[i]['topic'],
          wid: maps[i]['wid'],
          uid: maps[i]['uid'],
          nickname: maps[i]['nickname'],
          avatar: maps[i]['avatar'],
          content: maps[i]['content'],
          timestamp: maps[i]['timestamp'],
          type: maps[i]['type'],
          unreadCount: maps[i]['unreadCount'],
          client: maps[i]['client']);
    });
  }

  Future<int> delete(String? tid) async {
    return await database!
        .delete(tableThread!, where: '$columnTid = ?', whereArgs: [tid]);
  }

  Future<int> updateUnreadCount(String? tid) async {
    return await database!.rawUpdate(
        'UPDATE $tableThread SET $columnUnreadCount = $columnUnreadCount + 1 WHERE $columnTid = ?',
        [tid]);
  }

  Future close() async => database!.close();
}
