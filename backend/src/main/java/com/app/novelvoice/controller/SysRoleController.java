package com.app.novelvoice.controller;

import com.app.novelvoice.entity.SysRole;
import com.app.novelvoice.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @GetMapping("/list")
    public List<SysRole> list() {
        return roleService.selectRoleList();
    }

    @GetMapping("/{roleId}")
    public SysRole getInfo(@PathVariable Long roleId) {
        return roleService.selectRoleById(roleId);
    }

    @PostMapping
    public int add(@RequestBody SysRole role) {
        return roleService.insertRole(role);
    }

    @PutMapping
    public int edit(@RequestBody SysRole role) {
        return roleService.updateRole(role);
    }

    @DeleteMapping("/{roleId}")
    public int remove(@PathVariable Long roleId) {
        return roleService.deleteRoleById(roleId);
    }
}
