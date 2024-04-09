package com.hidecarbon.hidecarbon.user.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberDto {

    private Long userNo;

    private String userEmail;

    private String password;

    private String userName;

    private String userPhn;

    private String userRole;

    private String imgPath;

}
