package com.automate.df.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automate.df.entity.sales.lead.DmsAccount;

public interface DmsAccountDao extends JpaRepository<DmsAccount, Integer>{

}
