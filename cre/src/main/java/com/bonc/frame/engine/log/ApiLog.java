package com.bonc.frame.engine.log;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.framework.api.log.IApiLog;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ApiLog implements IApiLog{

	private Log log = LogFactory.getLog(getClass());

    private BlockingQueue<com.bonc.framework.api.log.entity.ApiLog> apiLogBlockQueue;

	private final String _mybitsId = "com.bonc.frame.engine.mapper.ApiMapper.";
	
	private DaoHelper daoHelper;
	
	private static final int DEFAULT_BATCH_SIZE = 100;
	
	private static final int DEFAULT_SIZE = 1024 * 3;
	
	public ApiLog(DaoHelper daoHelper) {
		this(daoHelper, DEFAULT_SIZE);
	}
	
	public ApiLog(DaoHelper daoHelper,int size) {
		this.daoHelper = daoHelper;
		if(size <= 0) {
			size = DEFAULT_SIZE;
		}
		apiLogBlockQueue = new ArrayBlockingQueue<>(size);
		ExecutorService es1 = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i ++) {
			es1.execute(new ApilogThread());
		}
	}

    @Deprecated
    @Override
    public void recordRuleLog(Map<String, Object> map, boolean isLog) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recordRuleLog(com.bonc.framework.api.log.entity.ApiLog apiLog, boolean isLog) {
        if(isLog) {
            try {
                boolean result = apiLogBlockQueue.offer(apiLog, 10, TimeUnit.MILLISECONDS);
                if(!result) {
                    log.warn("recordApiLog error,the queue is full." + apiLog);
                }
            } catch (InterruptedException e) {
                log.warn("recordApiLog exception:" + e);
            }
        }

    }

	private class ApilogThread implements Runnable {
		
		private Log log = LogFactory.getLog(this.getClass());

		@Override
		public void run() {
			try {
                List<com.bonc.framework.api.log.entity.ApiLog> apiLogList = new ArrayList<>(DEFAULT_BATCH_SIZE);
				while (true) {
					// drainTo():????????????BlockingQueue??????????????????????????????????????????????????????????????????????????????
					// ?????????????????????????????????????????????????????????????????????????????????????????????
                    com.bonc.framework.api.log.entity.ApiLog apiLog = apiLogBlockQueue.take();
					apiLogBlockQueue.drainTo(apiLogList, DEFAULT_BATCH_SIZE-1);
                    apiLogList.add(apiLog);
					// daoHelper.insertBatch(_mybitsId + "insertBatchApiLog", apiLogList);
					// apiLogList.clear();
					SqlSessionTemplate sst = daoHelper.getSqlSessionTemplate();
					SqlSession ss = sst.getSqlSessionFactory().openSession(false);// ?????????????????????false
					try {
                        ss.insert(_mybitsId + "insertBatchApiLog", apiLogList);
						/*for (com.bonc.framework.api.log.entity.ApiLog apiLog0 : apiLogList) {
							ss.insert(_mybitsId + "insertApiLog", apiLog0);
						}*/
						ss.commit();
					} catch (Exception e) {
						ss.rollback();
						log.error("api-??????????????????????????????", e.fillInStackTrace());
						log.error("api-????????????????????????????????????" + JsonUtils.toJSONNoFeatures(apiLogList));
					}finally {
						apiLogList.clear();
						ss.close();
					}
				}
				
			} catch (Exception e) {
				log.error(e);
			}
		}
		
	}

}
