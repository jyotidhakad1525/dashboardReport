package com.automate.df.dao.oh;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automate.df.entity.sales.lead.DmsDeliveryCheckList;


public interface DeliveryCheckListRepo extends JpaRepository<DmsDeliveryCheckList, Integer>{
	
}
