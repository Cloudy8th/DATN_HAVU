import Swal from 'sweetalert2/dist/sweetalert2.js'
import 'sweetalert2/src/sweetalert2.scss'

export const dialog = (title, icon, text, confirmButtonText = 'OK', showCancelButton = false) => {
    Swal.fire({
        title: title,
        text: text,
        icon: icon,
        showCancelButton: showCancelButton,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: confirmButtonText
    })
}

export const dialogConfirm = (title, text, callback, confirmButtonText = 'Đồng ý') => {
    Swal.fire({
        title: title,
        text: text,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: confirmButtonText,
        cancelButtonText: 'Hủy',
        preConfirm: async () => {
            callback()
        }
    })
}