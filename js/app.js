/**
 * app.js —— 登录页的 AJAX 逻辑
 *
 * 流程：
 *   1. 阻止表单默认提交（否则会整页刷新）
 *   2. 前端先做非空校验
 *   3. 用 fetch 把用户名/密码 POST 给后端 /api/login
 *   4. 拿到 JSON 后，根据 success 显示成功 / 失败提示
 */
(function () {
    'use strict';

    const form = document.getElementById('loginForm');
    const msg = document.getElementById('msg');
    const btn = document.getElementById('submitBtn');

    form.addEventListener('submit', function (event) {
        // 关键：阻止表单的默认提交行为，页面才不会跳转/刷新
        event.preventDefault();

        // 1. 前端校验（非空）
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        // 2. 组装要提交的数据
        const data = new URLSearchParams();
        data.append('username', document.getElementById('username').value.trim());
        data.append('password', document.getElementById('password').value.trim());

        // 3. 发送 AJAX 请求
        btn.disabled = true;
        btn.textContent = '登录中...';
        showMessage('', '');

        fetch('api/login', {
            method: 'POST',
            headers: {
                // 表单编码，后端可以用 request.getParameter 直接取
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
            },
            body: data.toString()
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                return response.json();
            })
            .then(function (result) {
                if (result.success) {
                    showMessage('alert-success', '登录成功，欢迎你，' + result.username + '！');
                } else {
                    showMessage('alert-danger', result.message);
                }
            })
            .catch(function (error) {
                showMessage('alert-danger', '请求失败：' + error.message);
            })
            .finally(function () {
                btn.disabled = false;
                btn.textContent = '登 录';
            });
    });

    /** 显示提示条；type 为空表示隐藏 */
    function showMessage(type, text) {
        msg.className = 'alert mt-3 mb-0';
        if (!type) {
            msg.classList.add('d-none');
            msg.textContent = '';
            return;
        }
        msg.classList.add(type);
        msg.textContent = text;
    }
})();
