package com.liao.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liao.ruiji.common.Result;
import com.liao.ruiji.pojo.Employee;
import com.liao.ruiji.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/employee")

@Slf4j
public class EmployeeController {

    // 继承了mp 父亲接口IService 故有了save等方法
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登入功能
     * @param request
     * @param employee
     * @return
     */
    //  发送post请求
    //  @RequestBody 主要用于接收前端传递给后端的json字符串（请求体中的数据）
    //  HttpServletRequest 作用：如果登录成功，将员工对应的id存到session一份，这样想获取一份登录用户的信息就可以随时获取出来
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        // 密码加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //这部分就是MP mybatis-plus  构建查询条件
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lqw);
        // 由于name是唯一约束、所以可以用getOne()

        if (emp == null) {
            return Result.error("登陆失败");
        }
        if (!emp.getPassword().equals(password)) {
            return Result.error("登录失败");
        }
        if (emp.getStatus() == 0) {
            return Result.error("该用户已被禁用");
        }
        //存个Session，只存个id就行了
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }


    /**
     * @param //request 删除request作用域中的session对象，就按登陆的request.getSession().setAttribute("employ",empResult.getId());删除employee就行
     * @return
     */
    //登出功能的后端操作很简单，只要删除session就好了
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }


    /**
     * @param //httpServletRequest 获取当前操作人员的session id用
     * @param //employee 将员工的数据解析为employee对象
     * 前端json{name: "", phone: "", sex: "", idNumber: "", username: ""}
     * @return
     */
    @PostMapping
    public Result<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增的员工信息：{}", employee.toString());
        //新增加员工时：设置默认密码为123456，并采用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置createTime和updateTime
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //根据session来获取创建人的id
        // request.getSession().setAttribute("employee",emp.getId());
        Long empId = (Long) request.getSession().getAttribute("employee");
        //并设置
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        // 以上代码可修改为：

        //存入数据库
        employeeService.save(employee);
        return Result.success("添加员工成功");
    }

    /**
     * 分页展示员工列表接口、查询某个员工
     * @param page 查询第几页
     * @param pageSize 每页一共几条数据
     * @param name 查询名字=name的数据
     * @return 返回Page页
     */

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（当我们没有输入name时，就相当于查询所有了）
        wrapper.like(!(name == null || "".equals(name)), Employee::getName, name);
        //并对查询的结果进行降序排序，根据更新时间
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, wrapper);
        return Result.success(pageInfo);
    }

    //更新员工状态 O  1
    /**
     * 更新员工状态，是PUT请求
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PutMapping()
    public Result<Employee> update(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
        System.out.println("更新"+Thread.currentThread().getName());
        //从Request作用域中拿到员工ID
        Long empId = (Long) httpServletRequest.getSession().getAttribute("employee");

        //拿新的状态值
        employee.setStatus(employee.getStatus());

        //更新时间
        employee.setUpdateTime(LocalDateTime.now());

        //更新处理人id
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return Result.success(employee);
    }



    /**
     * 拿到员工资料，前端自动填充列表，更新的时候复用上面的update方法
     * @param id ResultFul风格传入参数，用@PathVariable来接收同名参数
     * @return 返回员工对象
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmployee(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return Result.success(employee);
        }
        return Result.error("没有查到员工信息");
    }
}