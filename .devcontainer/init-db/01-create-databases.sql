-- Create databases
CREATE DATABASE IF NOT EXISTS sform CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE DATABASE IF NOT EXISTS sform_test CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- Grant privileges to sform user
GRANT ALL PRIVILEGES ON sform.* TO 'sform'@'%';
GRANT ALL PRIVILEGES ON sform_test.* TO 'sform'@'%';
FLUSH PRIVILEGES;
