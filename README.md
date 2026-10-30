# AJAX 用户登录

第 4 次作业：使用 AJAX 完成用户登录功能的开发。

## 一、项目结构

```
ajax-login
├── index.html                         登录页面（Bootstrap 样式 + 表单）
├── css/bootstrap.min.css              Bootstrap 样式
├── js/app.js                          ★ AJAX 逻辑（fetch 提交 + 处理 JSON 结果）
├── WEB-INF
│   ├── web.xml                        部署描述符
│   ├── src/com/example/LoginServlet.java   ★ 后端登录接口（源码）
│   └── classes/com/example/LoginServlet.class  编译后的字节码
└── 截图/                              运行效果截图
```

## 二、运行方式

1. 把整个 `ajax-login` 目录放到 Tomcat 的 `webapps` 下（可改名为 `ajaxlogin`）；
2. 启动 Tomcat（`bin/startup.bat`）；
3. 浏览器访问 <http://localhost:8080/ajaxlogin/>。

测试账号：`admin / 123456`、`user / 123456`、`teacher / 888888`。

环境：Tomcat 10.1.33（Jakarta EE 9+，包名 `jakarta.servlet.*`）+ JDK 24。

## 三、实现思路

### 1. 前端（js/app.js）

1. 监听表单的 `submit` 事件，用 `event.preventDefault()` 阻止默认提交（否则页面会刷新，就不是 AJAX 了）；
2. 先用 `form.checkValidity()` 做前端非空校验；
3. 用 `fetch()` 发 POST 请求到 `api/login`，请求体用 `URLSearchParams`（表单编码），后端可直接 `request.getParameter` 取值；
4. 请求期间把按钮置灰、文字改成“登录中...”，避免重复提交；
5. 拿到 JSON 后：`success` 为 true 显示绿色提示，否则显示后端返回的错误信息（红色提示）。

### 2. 后端（com.example.LoginServlet）

- `@WebServlet("/api/login")` 映射接口地址；
- 用 `request.setCharacterEncoding("UTF-8")` 防止中文乱码；
- 用 `response.setContentType("application/json;charset=UTF-8")` 告诉浏览器返回 JSON；
- 用 Map 模拟数据库中的用户表，依次校验：非空 → 用户名是否存在 → 密码是否正确；
- 登录成功时把用户名写入 session（`session.setAttribute("loginUser", username)`）；
- 手动拼接 JSON 返回（作业不引入 Gson/Jackson 等第三方库），返回值形如：

```json
{ "success": true, "message": "登录成功", "username": "admin" }
```

## 四、接口说明

| 项目 | 内容 |
| --- | --- |
| 地址 | `POST /api/login` |
| 参数 | `username`、`password`（application/x-www-form-urlencoded） |
| 成功返回 | `{"success":true,"message":"登录成功","username":"admin"}` |
| 失败返回 | `{"success":false,"message":"密码错误","username":"admin"}` |

## 五、验证结果

| 场景 | 返回 |
| --- | --- |
| `admin` / `123456` | `{"success":true,"message":"登录成功","username":"admin"}` |
| `admin` / `000000` | `{"success":false,"message":"密码错误","username":"admin"}` |
| 用户名密码为空 | `{"success":false,"message":"用户名和密码不能为空","username":""}` |

页面效果见 `截图/` 目录：初始状态、登录失败、登录成功。

## 六、说明

用户表用 Map 写死在代码里，是为了把重点放在 AJAX 的请求 / 响应流程上；
真实项目中应改为查询数据库（JDBC），密码也不能明文存储（应加盐哈希）。
