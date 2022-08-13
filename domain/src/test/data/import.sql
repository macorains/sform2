USE sform;

LOAD DATA LOCAL INFILE 'd_form.csv'
REPLACE INTO TABLE d_form
FIELDS TERMINATED BY ','
IGNORE 1 LINES
(@1,@2,@3,@4,@5,@6,@7,@8,@9,@10,@11,@12,@13,@14,@15,@16,@17,@18)
SET id=@1, hashed_id=@2, form_index=@3, name=@4, title=@5,
status=@6, cancel_url=@7, complete_url=@8, input_header=@9, confirm_header=@10,
complete_text=@11, close_text=@12, form_data=@13, user_group=@14, created_user=@15,
modified_user=@16, created=@17, modified=@18
;

