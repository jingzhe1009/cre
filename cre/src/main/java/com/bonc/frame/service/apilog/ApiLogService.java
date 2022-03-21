package com.bonc.frame.service.apilog;

import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/8 16:40
 */
public interface ApiLogService {

    Map<String, Object> getApiLogList(String apiName, String startDate, String endDate,
                                      String sort, String order,String start, String size);

}
