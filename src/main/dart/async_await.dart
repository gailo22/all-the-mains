import 'dart:io';

Future<ProcessedData> createData() async {
  try {
    final id = await _loadDataFromDisk();
    final data = await _fetchNetworkData(id);
    return ProcessedData(data);
  } on HttpException catch (err) {
    print("Network error: $err");
    return ProcessedData.empty();
  } finally {
    print("All done");
  }
}

Future<dynamic> _fetchNetworkData(int id) async {
}

Future<int> _loadDataFromDisk() async {
}

class ProcessedData {
  ProcessedData(data);

  static Future<ProcessedData> empty() {}
}

void main() {
  print("hello");
}