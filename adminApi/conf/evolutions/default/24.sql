# --- !Ups
ALTER TABLE `d_authtoken`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `d_form`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `d_form_col`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `d_form_col_select`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `d_form_col_validation`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `d_postdata`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `d_transfer_detail_log`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `d_transfer_log`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `d_transfer_tasks`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `m_authinfo`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `m_transfers`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `m_user`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `m_userinfo`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;
ALTER TABLE `play_evolutions`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_bin ;

# --- !Downs
ALTER TABLE `d_authtoken`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `d_form`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `d_form_col`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_0900_ai_ci ;
ALTER TABLE `d_form_col_select`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_0900_ai_ci ;
ALTER TABLE `d_form_col_validation`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_0900_ai_ci ;
ALTER TABLE `d_postdata`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `d_transfer_detail_log`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `d_transfer_log`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `d_transfer_tasks`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `m_authinfo`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `m_transfers`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `m_user`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `m_userinfo`
CHARACTER SET = utf8 , COLLATE = utf8_bin ;
ALTER TABLE `play_evolutions`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_0900_ai_ci ;
