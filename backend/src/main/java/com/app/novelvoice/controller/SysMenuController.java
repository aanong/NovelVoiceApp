package com.app.novelvoice.controller;

import com.app.novelvoice.entity.SysMenu;
import com.app.novelvoice.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @GetMapping("/list")
    public List<SysMenu> list(Long userId) {
        return menuService.selectMenuList(userId);
    }

    @GetMapping("/treeselect")
    public List<SysMenu> treeselect(Long userId) {
        return menuService.selectMenuTreeByUserId(userId);
    }

    @GetMapping("/{menuId}")
    public SysMenu getInfo(@PathVariable Long menuId) {
        return menuService.selectMenuById(menuId);
    }

    @PostMapping
    public int add(@RequestBody SysMenu menu) {
        return menuService.insertMenu(menu);
    }

    @PutMapping
    public int edit(@RequestBody SysMenu menu) {
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{menuId}")
    public int remove(@PathVariable Long menuId) {
        return menuService.deleteMenuById(menuId);
    }
}
