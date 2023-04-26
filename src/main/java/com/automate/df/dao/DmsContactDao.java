package com.automate.df.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automate.df.entity.sales.lead.DmsContact;

public interface DmsContactDao extends JpaRepository<DmsContact, Integer>{

}
