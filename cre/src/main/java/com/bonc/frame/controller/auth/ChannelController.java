package com.bonc.frame.controller.auth;


import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.auth.ChannelService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
/**
 * web界面：用户-渠道管理
 *
 *
 * */
@Controller
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    ChannelService channelService;

    @PermissionsRequires(value = "/chan", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, String childOpen, String tabId, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        model.addAttribute("tabId", tabId);
        return "/pages/authority/authority";
    }

    @PermissionsRequires(value = "/chan/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult save(@RequestBody Channel channel, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return channelService.save(channel, loginUserId);
    }

    @PermissionsRequires(value = "/chan/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request,String channelName, String start, String length) {
        Map<String, Object> result = channelService.list(request,channelName, start, length);
        return result;
    }
    
    @PermissionsRequires(value = "/chan/view", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/nameList", method = RequestMethod.GET)
    @ResponseBody
    public List<Object> nameList() {
        List<Object> result = channelService.nameList();
        return result;
    }

    @PermissionsRequires(value = "/chan/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult delete(String channelId) {
        return channelService.delete(channelId);
    }

    @PermissionsRequires(value = "/chan/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult update(@RequestBody Channel channel, HttpServletRequest request) {
        final String loginUserId = ControllerUtil.getLoginUserId(request);
        return channelService.update(channel, loginUserId);
    }

    // ------------------------------- 权限校验 -------------------------------
    //增加渠道权限
    @PermissionsRequires(value = "/chan/add", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/save/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult save() {
        return ResponseResult.createSuccessInfo();
    }
    //修改渠道权限
    @PermissionsRequires(value = "/place/update", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/update/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult update() {
        return ResponseResult.createSuccessInfo();
    }
    //删除渠道权限
    @PermissionsRequires(value = "/chan/delete", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/delete/checkAuth", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult delete() {
        return ResponseResult.createSuccessInfo();
    }
}
