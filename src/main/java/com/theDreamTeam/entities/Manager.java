package com.theDreamTeam.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

@Entity
public class Manager extends User implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExtendTimeRequest> extendTimeRequests;

    public Manager() { }    // ctor for hibernate

    public Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<ExtendTimeRequest> getExtendTimeRequests() {
        return extendTimeRequests;
    }
    
//    public void addExtendTimeRequest(ExtendTimeRequest...extendTimeRequests) {
//    	for(ExtendTimeRequest extendTimeRequest:  extendTimeRequests) {
//    		this.getExtendTimeRequests().add(extendTimeRequest);
//    	}
//    }

    public void setExtendTimeRequests(List<ExtendTimeRequest> extendTimeRequests) {
        this.extendTimeRequests = extendTimeRequests;
    }
}
