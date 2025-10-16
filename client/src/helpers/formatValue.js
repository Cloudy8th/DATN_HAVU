const formatValue = {
    formatMoney(value) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    },

    formatDateTime(date) {
        let dateTime = new Date(date);
        let dateString =
            ("0" + dateTime.getHours()).slice(-2) + ":" +
            ("0" + dateTime.getMinutes()).slice(-2) + ":" +
            ("0" + dateTime.getSeconds()).slice(-2) + " " +
            ("0" + dateTime.getDate()).slice(-2) + "/" +
            ("0" + (dateTime.getMonth() + 1)).slice(-2) + "/" +
            dateTime.getFullYear();
        return dateString;
    },
    formatDate(date) {
        let dateTime = new Date(date);
        let dateString =
            ("0" + dateTime.getDate()).slice(-2) + "/" +
            ("0" + (dateTime.getMonth() + 1)).slice(-2) + "/" +
            dateTime.getFullYear();
        return dateString;
    },
    formatTimestamp(date, dateOnly = true) {
        if (!date) return null;
        const d = new Date(date);

        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, "0");
        const day = String(d.getDate()).padStart(2, "0");
        let hours = String(d.getHours()).padStart(2, "0");
        let minutes = String(d.getMinutes()).padStart(2, "0");
        let seconds = String(d.getSeconds()).padStart(2, "0");

        let milliseconds = String(d.getMilliseconds()).padStart(3, "0");
        if (dateOnly) {
            hours = '00';
            minutes = '00';
            seconds = '00';
            milliseconds = '000';
        }

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
    }
}

export default formatValue;