import authService from "@/apis/services/authService";
import { defineStore } from "pinia";
import { dialog } from "@/helpers/swal";
import router from "@/routers/router";
import { useUserStore } from "./user";
import CONSTANT from "../helpers/constant";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    isLoggedIn: localStorage.getItem("user") ? true : false,
  }),
  getters: {
    getIsLoggedIn(state) {
      return state.isLoggedIn;
    },
  },
  actions: {
    async fetchLogin(data) {
      try {
        const res = await authService.login(data);
        if (res.status === 200) {
          const user = JSON.stringify(res.data);
          console.log(user);
          localStorage.setItem("user", user);
          this.isLoggedIn = true;
          const userStore = useUserStore();
          userStore.fetchSetUserId();
          await userStore.fetchGetById();
        }
      } catch (error) {
        dialog(
          "Đăng nhập thất bại",
          "error",
          error?.response?.data?.userMessage
        );
        console.error(error);
      }
    },

    fetchLogout(route) {
      this.isLoggedIn = false;
      localStorage.removeItem("user");
      localStorage.removeItem(CONSTANT.LOCALSTORAGE.CHAT_HISTORY);
      const userStore = useUserStore();
      userStore.fetchLogout();
      if (route) {
        const requireAuth = route.meta.authenticate;
        if (requireAuth) {
          router.push({ name: "Login", query: { redirect: route.fullPath } });
        }
      }
    },

    async fetchRegister(data) {
      try {
        const res = await authService.register(data);
        if (res.status === 201) {
          router.push({ name: "Login" });
          dialog("Đăng ký thành công", "success", null);
        }
        return res.data;
      } catch (error) {
        dialog("Đăng ký thất bại", "error", error?.response?.data?.userMessage);
        console.error(error);
      }
    },
  },
});
