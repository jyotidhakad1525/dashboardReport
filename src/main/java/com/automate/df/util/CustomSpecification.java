package com.automate.df.util;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;


public class CustomSpecification<T> {

    public static <T> Specification<T> hasOrgId(BigInteger val) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("organizationId"), val);
        };
    }
    
    public static <T> Specification<T> orgId(BigInteger val) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("dmsOrganization"), val);
        };
    }

    public static <T> Specification<T> hasBranch(BigInteger val) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("branchId"), val);
        };
    }
    
    
    public static <T> Specification<T> branch(String val) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("dmsbranch"), val);
        };
    }

    public static <T> Specification<T> isStatus(String val) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), val);
        };
    }

    public static <T> Specification<T> hasCustId(BigInteger val) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("customerId"), val);
        };
    }

    public static <T, V> Specification<T> attribute(String attrName, V val) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(attrName), val);
        };
    }

    public static <T, V> Specification<T> attributeLike(String attrName, V val) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get(attrName), "%" + val + "%");
        };
    }
    
    public static <T, V> Specification<T>orNames(String createdBy,String salesExecutiveName,String employee,List<String> value) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.or(
            	    criteriaBuilder.in(root.get(createdBy)).value(value),
            	    criteriaBuilder.in(root.get(salesExecutiveName)).value(value),
            	    criteriaBuilder.in(root.get(employee)).value(value)
//                    criteriaBuilder.equal(root.get(createdBy), value),
//                    criteriaBuilder.equal(root.get(salesExecutiveName), value),
//                    criteriaBuilder.equal(root.get(employee), value)
            );
        };

    }


    public static <T, V> Specification<T> between(String attrName, String from, String to) {
        return (root, query, criteriaBuilder) -> {
            Date from1= null;
            try {
                from1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(from);
                Date to1=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(to);
                return criteriaBuilder.between(root.get(attrName), from1, to1);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        };
    }
    
    public static <T, V> Specification<T> in(String attrName, List<String> stages) {
        return (root, query, criteriaBuilder) -> {
                return criteriaBuilder.in(root.get(attrName)).value(stages);
        };
    }

    public static <T, V> Specification<T> inInt(String attrName, ArrayList<Integer> stages) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.in(root.get(attrName)).value(stages);
        };
    }

    public static <T, V> Specification<T>or(String approved,String droppedby,String value) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get(approved), value),
                    criteriaBuilder.equal(root.get(droppedby), value)
            );
        };

    }
}
