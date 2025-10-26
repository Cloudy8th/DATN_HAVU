<template>
    <VueDatePicker
        :uid="props.id"
        v-model="dateInput"
        :format="timePicker == false ? format : null"
        locale="vi"
        :day-names="['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN']"
        :enable-time-picker="timePicker"
        month-name-format="long"
        auto-apply
        autocomplete="on"
        :teleport="true"
    />
</template>

<script setup>
import { ref } from "vue";

// ---------------------- Props ----------------------
const props = defineProps({
    id: String,
    timePicker: {
        type: Boolean,
        default: false,
    },
});

// ---------------------- Khai báo biến --------------
const dateInput = ref(null);

// ---------------------- Watcher --------------------

// ---------------------- Lifecycle ------------------

// ---------------------- Hàm xử lý ------------------
const format = (date) => {
    const day = date.getDate() >= 10 ? date.getDate() : "0" + date.getDate();
    const month =
        date.getMonth() + 1 >= 10
            ? date.getMonth() + 1
            : "0" + (date.getMonth() + 1);
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
};
</script>

<style scoped>
::deep(.vdp-datepicker__calendar) {
    position: fixed;
}
</style>
