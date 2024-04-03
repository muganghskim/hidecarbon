package com.hidecarbon.hidecarbon.redis.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberCashDto extends AllDtos {

    private Long userNo;

    private String userEmail;

    private String userName;

    private String userPhn;

    private String userRole;

}
