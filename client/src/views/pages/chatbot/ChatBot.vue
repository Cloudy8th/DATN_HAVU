<template>
  <div class="chatbot">
    <div class="chatbot-icon" @click="showChatbot">
      <img src="@/assets/imgs/chatbot.png" alt="" />
    </div>
    <div class="chatbot-box" v-if="isShowChatbot" v-click-outside="hideChatbot">
      <div class="box-header">
        <div class="title-header">
          <h4>Trợ lý AI</h4>
          <b-button
            icon="fa-solid fa-pen-to-square"
            @click="createNewChat"
            title="Cuộc trò chuyện mới"
          ></b-button>
        </div>
        <b-button
          icon="fa-solid fa-xmark"
          @click="hideChatbot"
          title="Đóng"
        ></b-button>
      </div>
      <div class="box-body">
        <div
          class="body-content"
          ref="contentBox"
          @click="handleClickContentbox"
        >
          <div
            v-for="(item, index) in data"
            :key="index"
            :class="[
              item.role == 'user' ? 'contentUser' : 'contentSystem',
              'content-item',
            ]"
          >
            <!-- <span class="time">{{ `${new Date(Date.now()).getHours()} : ${new
            Date(Date.now()).getMinutes()}` }}</span> -->
            <div class="content-data">
              <pre>{{ item.content }}</pre>
            </div>
            <dots-loader
              class="dots-loader"
              v-if="
                isLoading && item.role == 'user' && index === data.length - 1
              "
            ></dots-loader>
          </div>
        </div>
      </div>
      <div class="box-footer">
        <div class="box-message">
          <b-input
            ref="inputBox"
            type="text"
            v-model="contentUser"
            @keydown.enter="clickChat"
          ></b-input>
          <b-button
            :disabled="isLoading"
            icon="fa-solid fa-circle-arrow-right"
            @click="clickChat"
          ></b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onUpdated, onMounted } from "vue";
import { chatbot } from "@/apis/services/chatbot";
import CONSTANT from "@/helpers/constant";

// ------------------------- Khai báo biến ----------------------
const isShowChatbot = ref(false);
const data = ref([]);
const contentUser = ref("");
const isLoading = ref(false);

const inputBox = ref(null);
const contentBox = ref(null);
// ------------------------ Lifecle -----------------------------
onUpdated(() => {
  nextTick(() => {
    if (isShowChatbot.value === true) {
      contentBox.value.scrollTop = contentBox.value.scrollHeight;
    }
  });
});
onMounted(() => {
  const raw = localStorage.getItem(CONSTANT.LOCALSTORAGE.CHAT_HISTORY);
  if (raw) {
    try {
      data.value = JSON.parse(raw);
    } catch (e) {
      console.error(e);
    }
  }
});
// ------------------------ Watcher -----------------------------

// ------------------------ Hàm xử lý ---------------------------
const showChatbot = () => {
  isShowChatbot.value = true;
  nextTick(() => {
    inputBox.value.focus();
  });
};
const hideChatbot = () => {
  isShowChatbot.value = false;
};
const createNewChat = () => {
  data.value = [];
  localStorage.removeItem(CONSTANT.LOCALSTORAGE.CHAT_HISTORY);
};
const saveHistory = () => {
  // giới hạn tối đa 50 message cho gọn
  const trimmed = data.value.slice(-50);
  localStorage.setItem(
    CONSTANT.LOCALSTORAGE.CHAT_HISTORY,
    JSON.stringify(trimmed)
  );
};
const clickChat = async () => {
  const content = contentUser.value.trim();
  if (!content || isLoading.value) return;

  data.value = [...data.value, { role: "user", content }];
  saveHistory();

  contentUser.value = "";
  isLoading.value = true;

  try {
    // 👇 GỬI KÈM HISTORY
    const res = await chatbot(content, data.value);

    const botMsg =
      typeof res === "object" && res?.answer ? res.answer : String(res ?? "");
    data.value = [...data.value, { role: "bot", content: botMsg }];
    saveHistory();
  } catch (error) {
    data.value = [
      ...data.value,
      { role: "bot", content: "Đã xảy ra lỗi! Vui lòng thử lại." },
    ];
    saveHistory();
    console.error(error);
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.chatbot {
  position: fixed;
  bottom: 20px;
  right: 30px;
}

.chatbot-icon {
  border-radius: 50%;
  width: 55px;
  height: 55px;
  cursor: pointer;
  z-index: 500;
  position: relative;
}

.chatbot-icon > img {
  width: inherit;
  height: inherit;
  object-fit: contain;
  border-radius: 50%;
  box-shadow: 0 5px 10px var(--color-box-shadow);
  border: 1px solid var(--color-primary);
  padding: 5px;
  background-color: var(--color-white);
}

.chatbot-box {
  z-index: 500;
  width: var(--width-chat-box);
  height: var(--height-chat-box);
  background-color: var(--color-white);
  position: absolute;
  bottom: 55px;
  right: 10px;
  border-radius: var(--border-radius);
  box-shadow: 0 10px 20px var(--color-box-shadow);
}

.box-header {
  height: 36px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--bg-main);
  border-top-left-radius: var(--border-radius);
  border-top-right-radius: var(--border-radius);
}

.box-header .title-header {
  display: flex;
  gap: 10px;
  align-items: center;
}

.box-header .title-header > h4 {
  padding-left: 10px;
  color: var(--color-primary);
  margin: 0;
}

.box-header > button,
.box-header .title-header > button {
  background-color: transparent;
}

.box-body {
  height: calc(var(--height-chat-box) - 86px);
}

.box-body .body-content {
  overflow-y: auto;
  display: block;
  height: 100%;
  padding: 5px 0;
}

.box-body .body-content > .content-item {
  width: 75%;
  margin: 5px;
}

.box-body .body-content .content-item > .content-data {
  width: fit-content;
  max-width: 100%;
  padding: 8px 10px;
  border-radius: var(--border-radius-page);
}

.box-body .body-content .content-item .content-data > pre {
  width: inherit;
  text-wrap: wrap;
  font-size: 1rem;
}

.contentSystem {
  float: left;
}

.contentSystem > .content-data {
  background-color: var(--bg-main);
  float: left;
}

.contentSystem .content-data > h5 {
  color: var(--color-primary);
  font-size: 0.9rem;
  padding-bottom: 5px;
  width: fit-content;
}

.contentUser {
  float: right;
  position: relative;
}

.dots-loader {
  position: absolute;
  bottom: -20px;
  left: -30%;
}

.contentUser > .content-data {
  float: right;
  background-color: var(--color-primary);
  color: var(--color-white);
}

.box-footer {
  height: 50px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  width: 100%;
  background-color: var(--bg-main);
  border-bottom-left-radius: var(--border-radius);
  border-bottom-right-radius: var(--border-radius);
}

.box-footer .box-message {
  display: flex;
  align-items: center;
  width: 100%;
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  background-color: var(--color-white);
}

.box-footer .box-message:focus-within {
  outline: 1px solid var(--color-primary-focus);
}

.box-footer .box-message > input {
  border: none;
}

.box-footer .box-message > button {
  border-radius: var(--border-radius);
  width: 34px;
  height: 34px;
  background-color: var(--bg-main);
  margin: 2px;
  color: var(--color-primary);
}
</style>
