<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="register">
                    <h1 class="h3 mb-5 fw-normal">Register</h1>

                    <div class="form-floating">
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                        <label for="username">用户名</label>
                    </div>
                    <div class="form-floating">
                        <input v-model="password" type="password" class="form-control" id="password"
                            placeholder="请输入密码">
                        <label for="password">密码</label>
                    </div>
                    <div class="form-floating">
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword"
                            placeholder="请再次输入密码">
                        <label for="confirmedPassword">确认密码</label>
                    </div>

                    <div class="error-message">{{ error_message }}</div>
                    <button class="w-100 btn btn-lg btn-primary" type="submit">注册</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import { ref } from 'vue'
import router from '@/router/index'
import $ from 'jquery'

export default {
    components: {
        ContentField
    },
    setup() {
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('');
        let error_message = ref('');

        const register = () => {
            $.ajax({
                url: 'http://127.0.0.1:3000/api/user/account/register/',
                type: "post",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        router.push({ name: 'user_account_login' })
                    }
                    else {
                        error_message.value = resp.error_message;
                    }
                }
            });
        }

        return {
            username,
            password,
            confirmedPassword,
            error_message,
            register
        }
    }
}
</script>

<style scoped>
button {
    margin-top: 50px;
}

div.error-message {
    color: red;
}
</style>