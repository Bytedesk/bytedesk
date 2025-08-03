#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import sys

def validate_jmx(file_path):
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()
        
        # 检查TestPlan
        test_plan = root.find('.//TestPlan')
        if test_plan is None:
            print("❌ 找不到TestPlan元素")
            return False
            
        # 检查用户定义变量
        user_vars = test_plan.find('.//elementProp[@name="TestPlan.user_defined_variables"]')
        if user_vars is None:
            print("❌ 找不到用户定义变量配置")
            return False
            
        # 检查是否有空的name属性
        empty_names = root.findall('.//elementProp[@name=""]')
        if empty_names:
            print(f"❌ 找到 {len(empty_names)} 个空的name属性")
            return False
            
        print("✅ 文件验证通过")
        return True
        
    except Exception as e:
        print(f"❌ 验证失败: {e}")
        return False

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("用法: python validate_fix.py <jmx_file>")
        sys.exit(1)
        
    success = validate_jmx(sys.argv[1])
    sys.exit(0 if success else 1) 