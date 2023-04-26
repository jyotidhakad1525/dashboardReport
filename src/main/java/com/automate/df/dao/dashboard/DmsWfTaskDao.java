package com.automate.df.dao.dashboard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.automate.df.entity.dashboard.DmsWFTask;

/**
 * 
 * @author sruja
 *
 */
public interface DmsWfTaskDao extends JpaRepository<DmsWFTask, Integer> {
	
	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id=:assigneeId and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime", nativeQuery = true)
	List<DmsWFTask> getWfTaskByAssigneeId(
			@Param(value = "assigneeId") String assigneeId,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	
	
	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id in (:assigneeIdList) and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime and task_status='CLOSED'", nativeQuery = true)
	List<DmsWFTask> getWfTaskByAssigneeIdList(
			@Param(value = "assigneeIdList") List<Integer> assigneeIdList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	
	
	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id in (:assigneeIdList) and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime and universal_id in (:universalIdList)", nativeQuery = true)
	List<DmsWFTask> getWfTaskByAssigneeIdListByModel(
			@Param(value = "assigneeIdList") List<Integer> assigneeIdList,
			@Param(value = "universalIdList") List<String> universalIdList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
		
	
	

	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id =:assigneeId \r\n"
			+ "	and task_status != 'CLOSED' \r\n"
			+ "	and task_created_time>= :startTime  and task_created_time <= :endTime", nativeQuery = true)
	List<DmsWFTask> getTodaysUpcomingTasks(
			@Param(value = "assigneeId") Integer assigneeId,
			//@Param(value = "universalIdList") List<String> universalIdList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);

	@Query(value = "SELECT * FROM dms_workflow_task  where universal_id=:universalId and task_name=:taskName", nativeQuery = true)
	List<DmsWFTask> getWfTaskByUniversalIdandTask(@Param(value = "universalId") String crmUniversalId, 
			@Param(value = "taskName") String str);
	
	@Query(value = "SELECT * FROM dms_workflow_task  where universal_id=:universalId and task_name=:taskName and task_status='CLOSED'", nativeQuery = true)
	List<DmsWFTask> getWfTaskByUniversalIdandTaskV2(@Param(value = "universalId") String crmUniversalId, 
			@Param(value = "taskName") String str);
	
	@Query(value = "SELECT * FROM dms_workflow_task where task_name=:taskName and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime", nativeQuery = true)
	List<DmsWFTask> getWfTaskByTaskName(
			@Param(value = "taskName") String taskName,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	
	@Query(value = "SELECT * FROM dms_workflow_task where task_name=:taskName and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime and universal_id in (:universalIdList) and task_status='CLOSED'", nativeQuery = true)
	List<DmsWFTask> getWfTaskByTaskNameAndUniversalIds(
			@Param(value = "taskName") String taskName,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime,
			@Param(value = "universalIdList") List<String> universalIdList);


	
	@Query(value = "select customer_remarks from salesDataSetup.dms_workflow_task where task_name=:task and universal_id=:id", nativeQuery = true)
	String getRemarksByUniversalIdAndStage(@Param(value = "id") String crmUniversalId, @Param(value = "task") String task);

}
