<template>
  <nav class="navbar navbar-expand-lg bg-body-tertiary mb-4">
    <div class="container-fluid">
      <a class="navbar-brand" href="#">SForm</a>
      <template v-if="hasMenu">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item">
              <a class="nav-link active" aria-current="page" href="/form">フォーム</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/admin">管理</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">ログアウト</a>
            </li>
          </ul>
        </div>
      </template>
    </div>
  </nav>
</template>
<script setup>
defineProps({
  hasMenu: Boolean
})
</script>
