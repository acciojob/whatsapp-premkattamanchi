package com.driver;

import java.util.*;
import java.sql.Date;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String,User> user;
    private HashMap<Integer,Message> messageMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.user = new HashMap<>();
        this.messageMap=new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public String createUser(String name, String mobile) throws Exception {
        if(user.containsKey(mobile))
            throw new Exception("User already exists");
        user.put(mobile,new User(name,mobile));
        return "SUCCESS";
    }
    public Group createGroup(List<User> users){
        String groupName="";
            if(users.size()==2)
                groupName=users.get(1).getName();
            else {
                customGroupCount++;
                groupName = "Group " + customGroupCount;
            }
            Group group=new Group(groupName,users.size());
            adminMap.put(group,users.get(0));
            groupUserMap.put(group,users);
        return group;
    }
    public int createMessage(String content){
        messageId++;
        long millis=System.currentTimeMillis();
        Date date =new Date(millis);
        messageMap.put(messageId,new Message(messageId,content,date));
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        String groupName=group.getName();
        String senderMobile= sender.getMobile();
        List<User> l;
        boolean isGroupFound=false;
        boolean isSenderFound=false;
        for(Group g:groupUserMap.keySet()){
            if(g.getName().equals(groupName)) {
                isGroupFound=true;
                l=groupUserMap.get(g);
                for(User u:l){
                    if(senderMobile.equals(u.getMobile())){
                        isSenderFound=true;
                        break;
                    }
                }
            }
        }
        if(isGroupFound==false)
            throw new Exception("Group does not exist");
        if(isSenderFound==false)
            throw new Exception("You are not allowed to send message");
        groupMessageMap.get(group).add(message);
        senderMap.put(message,sender);
        return groupMessageMap.get(group).size();
    }
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        String admin=adminMap.get(group).getMobile();
        if(!admin.equals(approver.getMobile()))
           throw new Exception("Approver does not have rights");
        if(!groupUserMap.get(group).contains(user))
            throw new Exception("User is not a participant");
        adminMap.put(group,user);
        return "SUCCESS";
    }
}
