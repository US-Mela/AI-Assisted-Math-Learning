import 'package:dio/dio.dart';
import 'package:mela/data/network/constants/endpoints_const.dart';
import 'package:mela/data/network/dio_client.dart';
import 'package:mela/domain/entity/topic/topic_list.dart';

class TopicApi {
  final DioClient _dioClient;
  TopicApi(this._dioClient);
  Future<TopicList> getTopics() async {
    final responseData = await _dioClient.get(
      EndpointsConst.getTopics,
    );
    return TopicList.fromJson(responseData['data']);
  }
}
