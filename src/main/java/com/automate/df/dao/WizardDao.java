package com.automate.df.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automate.df.entity.sales.WizardEntity;

public interface WizardDao extends JpaRepository<WizardEntity, Integer> {

}
