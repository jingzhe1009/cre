package com.bonc.frame.engine.log;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.entity.RuleLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 规则日志接口实现
 * @author qxl
 * @date 2018年4月24日 下午4:29:43
 * @version 1.0
 */
public class CreRuleLog implements IRuleLog {
	private Log log = LogFactory.getLog(getClass());
	
	private BlockingQueue<RuleLog> ruleLogBlockQueue;
	
	private BlockingQueue<RuleLogDetail> ruleLogDetailBlockQueue;
	
	private final String _mybitsId = "com.bonc.frame.engine.mapper.RuleLogMapper.";
	
	private DaoHelper daoHelper;
	
	private static final int DEFAULT_BATCH_SIZE = 100;
	
	private static final int DEFAULT_SIZE = 1024 * 10;
	
	public CreRuleLog(DaoHelper daoHelper) {
		this(daoHelper, DEFAULT_SIZE);
	}
	
	public CreRuleLog(DaoHelper daoHelper,int size) {
		this.daoHelper = daoHelper;
		if(size <= 0) {
			size = DEFAULT_SIZE;
		}
		ruleLogBlockQueue = new ArrayBlockingQueue<>(size);
		ruleLogDetailBlockQueue = new ArrayBlockingQueue<>(size * 10);
		ExecutorService es1 = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i ++) {
			es1.execute(new RulelogThread());
		}
		ExecutorService es2 = Executors.newFixedThreadPool(20);
		for (int i = 0; i < 20; i ++) {
			es2.execute(new RulelogDetailThread());
		}
	}

	@Override
	public void recordRuleLog(RuleLog ruleLog, boolean isLog) {
		if(isLog) {
			try {
				boolean result = ruleLogBlockQueue.offer(ruleLog, 10, TimeUnit.MILLISECONDS);
				if(!result) {
					log.error("recordRuleLog error,the queue is full."+ruleLog);
				}
			} catch (InterruptedException e) {
				log.error("recordRuleLog exception:"+e);
			}
		}

	}

	@Override
	public void recordRuleDetailLog(RuleLogDetail ruleLogDetail, boolean isLog) {
		if(isLog) {
			try {
				boolean result = ruleLogDetailBlockQueue.offer(ruleLogDetail, 10, TimeUnit.MILLISECONDS);
				if(!result) {
					log.warn("recordRuleDetailLog error,the queue is full." + ruleLogDetail);
				}
			} catch (InterruptedException e) {
				log.warn("recordRuleDetailLog exception:" + e);
			}
		}

	}

	@Override
	public void saveModelLog(Map<String, String> map,RuleLog ruleLog) {

	}

	private class RulelogThread implements Runnable {

		@Override
		public void run() {
			try {
				List<RuleLog> ruleLogList = new ArrayList<RuleLog>(DEFAULT_BATCH_SIZE);
				while (true) {
					// drainTo():一次性从BlockingQueue获取所有可用的数据对象（还可以指定获取数据的个数），
					// 通过该方法，可以提升获取数据效率；不需要多次分批加锁或释放锁。
					RuleLog ruleLog1 = ruleLogBlockQueue.take();
					ruleLogBlockQueue.drainTo(ruleLogList, DEFAULT_BATCH_SIZE-1);
					ruleLogList.add(ruleLog1);
					SqlSessionTemplate sst = daoHelper.getSqlSessionTemplate();
					SqlSession ss = sst.getSqlSessionFactory().openSession(false);// 设置自动提交为false
					try {
                        ss.insert(_mybitsId + "insertBatchRuleLog", ruleLogList);
						/*for (RuleLog ruleLog : ruleLogList) {
							ss.insert(_mybitsId + "insertRuleLog", ruleLog);
						}*/
						ss.commit();
					} catch (Exception e) {
						ss.rollback();
						log.error("ruleLog-日志保存数据库异常。", e.fillInStackTrace());
						log.error("ruleLog-日志保存数据库异常。数据" + JsonUtils.toJSONNoFeatures(ruleLogList));
					}finally {
						ruleLogList.clear();
						ss.close();
					}
				}

			} catch (Exception e) {
				log.error(e);
			}

		}
		
	}
	
	private class RulelogDetailThread implements Runnable {

		@Override
		public void run() {
			try {
				List<RuleLogDetail> ruleLogDetailList = new ArrayList<RuleLogDetail>(DEFAULT_BATCH_SIZE * 5);
				while (true) {
					// drainTo():一次性从BlockingQueue获取所有可用的数据对象（还可以指定获取数据的个数），
					// 通过该方法，可以提升获取数据效率；不需要多次分批加锁或释放锁。
					RuleLogDetail rld1 = ruleLogDetailBlockQueue.take();
					ruleLogDetailBlockQueue.drainTo(ruleLogDetailList, DEFAULT_BATCH_SIZE * 5 - 1);
					ruleLogDetailList.add(rld1);
					SqlSessionTemplate sst = daoHelper.getSqlSessionTemplate();
					SqlSession ss = sst.getSqlSessionFactory().openSession(false);// 设置自动提交为false
					try {
                        ss.insert(_mybitsId + "insertBatchRuleLogDetail", ruleLogDetailList);
						/*for (RuleLogDetail rld : ruleLogDetailList) {
							ss.insert(_mybitsId + "insertRuleLogDetail", rld);
						}*/
						ss.commit();
					} catch (Exception e) {
						ss.rollback();
						log.error("rulDetaileLog-日志保存数据库异常。", e.fillInStackTrace());
						log.error("ruleDetailLog-日志保存数据库异常。数据" + JsonUtils.toJSONNoFeatures(ruleLogDetailList));
					} finally {
						ruleLogDetailList.clear();
						ss.close();
					}
				}
				
			} catch (Exception e) {
				log.error(e);
			}
		}
		
	}

}
