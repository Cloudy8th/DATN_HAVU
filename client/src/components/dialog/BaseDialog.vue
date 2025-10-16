<template>
    <div class="dialog">
        <div class="dialog__container " :class="hideEffect ? 'hide-dialog' : 'show-dialog'"
            v-click-outside="beforeCloseDialog">
            <div class="dialog__header">
                <h3>{{ titleDialog }}</h3>
                <b-button icon="icon icon-close" title="Đóng" @click="beforeCloseDialog">
                </b-button>
            </div>
            <div class="dialog__body">
                <i :class="[iconDialog, type, 'icon']"></i>
                <p>{{ content }}</p>
            </div>
            <div class="dialog__footer">
                <div class="dialog__footer--left">
                    <b-button v-if="btnExtraLeft !== null" type="secondary" :value="btnExtraLeft"
                        @click="beforeCloseDialog"></b-button>
                </div>
                <div class="dialog__footer--right">
                    <b-button v-if="btnExtraRight !== null" type="secondary" :value="btnExtraRight"
                        @click="beforeCloseDialog(); handleBtnExtraRight()"></b-button>
                    <b-button ref="refBtnPrimary" v-if="btnPrimary !== null" type="primary" :value="btnPrimary"
                        @click="beforeCloseDialog(); handleBtnPrimary($event)"></b-button>
                </div>

            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "BaseDialog",
    props: {
        // Nội dung Dialog
        content: String,
        // kiểu Dialog (error, warning, confirm)
        type: {
            type: String,
            default: null
        },
        // tên nút bấm phụ bên trái
        btnExtraLeft: {
            type: String,
            default: null
        },
        // tên nút bấm phụ bên phải
        btnExtraRight: {
            type: String,
            default: null
        },
        // tên nút bấm chính
        btnPrimary: {
            type: String,
            default: null
        },
    },
    created() {
        this.setTitleDialog(this.type);
    },
    mounted() {
        /**
         * Tự động focus vào nút chính khi mở Dialog
         * Created by: DDHUY (16/09/2023)
         */
        this.$refs.refBtnPrimary.focus(true);
    },
    methods: {
        /**
         * Hàm xử lý sự kiện nút phụ bên phải
         * Created by: DDHUY (06/09/2023)
         */
        handleBtnExtraRight() {
            this.$emit("clickBtnExtraRight");
        },
        /**
         * Hàm xử lý sự kiện nút chính
         * Created by: DDHUY (06/09/2023)
         */
        handleBtnPrimary() {
            this.$emit("clickBtnPrimary");
        },
        /**
         * Hàm đặt tên Dialog theo type
         * Created by: DDHUY (06/09/2023)
         * @param {string} type
         */
        setTitleDialog(type) {
            switch (type) {
                case 'error':
                    this.titleDialog = "Lỗi";
                    this.iconDialog = "fa-solid fa-circle-xmark";
                    break;
                case 'warning':
                    this.titleDialog = "Cảnh báo";
                    this.iconDialog = "fa-solid fa-triangle-exclamation";
                    break;
                case 'confirm':
                    this.titleDialog = "Xác nhận";
                    this.iconDialog = "fa-solid fa-circle-question";
                    break;
                case 'success':
                    this.titleDialog = "Thành công";
                    this.iconDialog = "fa-solid fa-circle-check";
                    break;
                default:
                    break;
            }
        },
        /**
         * Tạo hiệu ứng khi đóng thông báo Dialog
         * Created by: DDHUY
         */
        beforeCloseDialog() {
            this.hideEffect = true;
            setTimeout(() => {
                this.$emit("closeDialog");
            }, 300);
        },
    },
    data() {
        return {
            hideEffect: false,
            titleDialog: '',
            iconDialog: '',
        }
    },
}
</script>

<style src="./dialog.css"></style>