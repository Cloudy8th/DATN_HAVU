<template>
  <footer id="footer">
    <div class="footer-content">
      <div class="footer-contact">
        <a target="_blank" href="https://maps.app.goo.gl/FgHi5QRvzM3T4jcm6">
          Vũ Dương - Ninh Bình
          <i class="fa-solid fa-location-dot"></i>
        </a>
        <a href="tel:0965530897">
          0965530897
          <i class="fa-solid fa-phone"></i>
        </a>
        <a href="mailto:vuha74486@gmail.com">
          vuha74486@gmail.com
          <i class="fa-solid fa-envelope"></i>
        </a>
      </div>
      <div class="footer-about">
        <p>
          HMart là một trang web mua sắm trực tuyến chuyên cung cấp các sản phẩm
          công nghệ hàng đầu, mang đến cho khách hàng trải nghiệm mua sắm thuận
          tiện và đa dạng. Với danh mục sản phẩm đa dạng từ điện thoại di động,
          máy tính xách tay, đồ điện tử gia dụng đến phụ kiện công nghệ, HMart
          cam kết mang đến cho người tiêu dùng những sản phẩm chất lượng với giá
          cả hợp lý.
        </p>
      </div>
    </div>
    <div class="footer-center">
      <a target="_blank" href="https://www.facebook.com/"
        ><i class="fa-brands fa-facebook"></i
      ></a>
      <a target="_blank" href="https://github.com/"
        ><i class="fa-brands fa-github"></i
      ></a>
      <a target="_blank" class="insta" href="https://www.instagram.com/"
        ><i class="fa-brands fa-instagram"></i
      ></a>
    </div>
    <div class="copyright">
      <p>&copy; 2025 - Bản quyền thuộc về HMart</p>
    </div>
  </footer>
</template>

<script>
export default {
  name: "TheFooter",
};
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

.footer-contact > a {
  padding-bottom: 10px;
}

.footer-contact > a > i {
  font-size: 1.5rem;
  padding-left: 5px;
}

.footer-about {
  width: 50%;
}

.footer-about > p {
  width: 80%;
}

.footer-center {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 10px;
  padding: 20px 0;
}

.footer-center > a {
  display: block;
  background-color: var(--color-white);
  padding: 6px;
  border-radius: 50%;
}

.footer-center > a > i {
  font-size: 2rem;
}

.footer-center > a > i.fa-facebook {
  color: #3b5998;
}

.footer-center > a > i.fa-github {
  color: #161414;
}

.footer-center > a.insta {
  background: linear-gradient(#f9ce34, #ee2a7b, #6228d7);
}

.footer-center > a > i.fa-instagram {
  color: var(--color-white);
}

#footer .copyright {
  text-align: center;
  font-size: 1.1rem;
  background-color: var(--color-primary-focus);
  padding: 10px 0;
}
</style>
