package com.automate.df.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.automate.df.dao.LostSubLostRepository;
import com.automate.df.entity.LostReasons;

@Service
@Transactional
public class LostSubLostServices {
	private final LostSubLostRepository lostsublostRepo;
	public LostSubLostServices(LostSubLostRepository lostsublostRepo) {
        this.lostsublostRepo = lostsublostRepo;  
    }
	public List<LostReasons> getAllSubLostAllDetails(String orgId,String stageName) {
        return lostsublostRepo.getAllSubLost(orgId,stageName);
    }

}