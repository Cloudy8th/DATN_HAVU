<template>
    <footer id="footer">
        <div class="footer-content">
            <div class="footer-contact">
                <a target="_blank" href="https://maps.app.goo.gl/1JJCTBmziiDaTxL77">
                    Kim Anh – Kim Thành - Hải Dương
                    <i class="fa-solid fa-location-dot"></i>
                </a>
                <a href="tel:098765432">
                    098765432
                    <i class="fa-solid fa-phone"></i>
                </a>
                <a href="mailto:acuaaaaaa0505@gmail.com">
                    acuaaaaaa0505@gmail.com
                    <i class="fa-solid fa-envelope"></i>
                </a>
            </div>
            <div class="footer-about">
                <p>
                    QPRO là website thương mại điện tử chuyên cung cấp các sản phẩm công nghệ chính hãng, từ phụ kiện
                    điện tử, thiết bị thông minh đến linh kiện máy tính và đồ chơi công nghệ. Với tiêu chí <b>"Chất
                        lượng -
                        Nhanh chóng - Uy tín"</b>, QPRO cam kết mang đến cho khách hàng trải nghiệm mua sắm tiện lợi,
                    giá cả
                    cạnh tranh cùng dịch vụ hậu mãi chuyên nghiệp. Dù bạn là người yêu công nghệ hay chỉ đang tìm kiếm
                    thiết bị hữu ích cho cuộc sống hiện đại, QPRO đều có giải pháp dành cho bạn.
                </p>
            </div>

        </div>
        <div class="footer-center">
            <a target="_blank" href="https://www.facebook.com/"><i class="fa-brands fa-facebook"></i></a>
            <a target="_blank" href="https://github.com/"><i class="fa-brands fa-github"></i></a>
            <a target="_blank" class="insta" href="https://www.instagram.com/"><i
                    class="fa-brands fa-instagram"></i></a>
        </div>
        <div class="copyright">
            <p>&copy; 2025 - Bản quyền thuộc về QPRO</p>
        </div>
    </footer>
</template>

<script>
export default {
    name: 'TheFooter'
}
</script>

<style scoped>
#footer {
    background-color: var(--color-primary);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    color: var(--color-white);
}

#footer a {
    color: var(--color-white);
    text-decoration: none;
    display: block;
}

.footer-content {
    display: flex;
    justify-content: center;
    gap: 20px;
    padding-top: 20px;
    line-height: 20px;
}

.footer-contact {
    text-align: right;
    width: 50%;
}

.footer-contact>a {
    padding-bottom: 10px;
}

.footer-contact>a>i {
    font-size: 1.5rem;
    padding-left: 5px;
}

.footer-about {
    width: 50%;
}

.footer-about>p {
    width: 80%;
}

.footer-center {
    width: 100%;
    display: flex;
    justify-content: center;
    gap: 10px;
    padding: 20px 0;
}

.footer-center>a {
    display: block;
    background-color: var(--color-white);
    padding: 6px;
    border-radius: 50%;
}

.footer-center>a>i {
    font-size: 2rem;
}

.footer-center>a>i.fa-facebook {
    color: #3b5998;
}

.footer-center>a>i.fa-github {
    color: #161414;
}

.footer-center>a.insta {
    background: linear-gradient(#f9ce34, #ee2a7b, #6228d7);
}

.footer-center>a>i.fa-instagram {
    color: var(--color-white);
}

#footer .copyright {
    text-align: center;
    font-size: 1.1rem;
    background-color: var(--color-primary-focus);
    padding: 10px 0;
}
</style>