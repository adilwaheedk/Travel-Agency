package com.visionxoft.abacus.rehmantravel.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class to store Franchise job/business record attributes for franchise registration
 */
public class FranchiseRecord {
    public List<FranchiseEmpBus> empBusList;
    public String quest1;
    public String quest2;
    public String quest3;
    public boolean retail_exp;
    public boolean franchise_exp;
    public String investment;
    public String sales_turnover;
    public String gross_income;
    public String no_of_emp;
    public ArrayList<File> attach_files;

    class FranchiseEmpBus {
        public String period;
        public String name;
        public String position;
    }
}


