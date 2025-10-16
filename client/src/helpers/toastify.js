import { toast } from "vue3-toastify";
import "vue3-toastify/dist/index.css";

export const toastify = (title, type = 'info', autoClose = 2500, position = 'top-center',) => {
    toast(title, {
        "theme": "colored",
        "type": type,
        "position": position,
        "autoClose": autoClose,
        "dangerouslyHTMLString": true
    })
}