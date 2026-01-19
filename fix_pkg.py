import os

root_dir = r"d:\code\NovelVoiceApp\excel-spring-boot-starter"
old_pkg = "com.app.tool.excel"
new_pkg = "com.gmrfid.excel"

for root, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith((".java", ".xml", ".properties", ".factories", ".yml", ".md")):
            file_path = os.path.join(root, file)
            try:
                # Try reading as UTF-8 first
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
            except UnicodeDecodeError:
                # Fallback to gbk (often default on Chinese Windows)
                with open(file_path, 'r', encoding='gbk') as f:
                    content = f.read()
            
            # Replace package
            new_content = content.replace(old_pkg, new_pkg)
            
            # Write back as UTF-8 without BOM
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)

print("Package replacement and encoding fix completed.")
