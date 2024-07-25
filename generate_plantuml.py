import os
import javalang
from pathlib import Path

def parse_java_file(file_path):
    try:
        with open(file_path, 'r') as f:
            content = f.read()
            tree = javalang.parse.parse(content)
            return tree, file_path
    except (javalang.parser.JavaSyntaxError, IOError) as e:
        print(f"Error parsing Java file {file_path}: {e}")
        return None, file_path

def parse_kotlin_file(file_path):
    try:
        with open(file_path, 'r') as f:
            content = f.read()
            kotlin_classes = []
            kotlin_methods = []
            kotlin_fields = []

            lines = content.splitlines()
            i = 0
            while i < len(lines):
                line = lines[i].strip()
                if line.startswith('class '):
                    kotlin_classes.append(line.split()[1].split('(')[0])
                elif 'val ' in line or 'var ' in line:
                    parts = line.split()
                    val_var_index = parts.index('val') if 'val' in parts else parts.index('var')
                    field_name = parts[val_var_index + 1].strip(',').split('=')[0]

                    field_type = None
                    if ':' in line:
                        colon_index = line.index(':')
                        equals_index = line.find('=', colon_index)
                        if equals_index == -1:
                            field_type = line[colon_index + 1:].strip()
                        else:
                            field_type = line[colon_index + 1:equals_index].strip()
                    
                    if field_type is None or '(' in field_type or ')' in field_type:
                        field_entry = f"{field_name}"
                    else:
                        field_entry = f"{field_name} : {field_type}"

                    kotlin_fields.append(field_entry)
                elif 'fun ' in line and '(' in line:
                    func_start_index = line.find('fun ') + 4
                    func_end_index = line.find('(')
                    func_signature = line[func_start_index:func_end_index].strip()
                    func_name = func_signature.split()[-1]

                    params_start = line.find('(')
                    params_end = line.find(')')
                    params = line[params_start+1:params_end]
                    param_list = []
                    for param in params.split(','):
                        param_parts = param.strip().split(':')
                        if len(param_parts) == 2:
                            param_name = param_parts[0].strip()
                            param_type = param_parts[1].strip()
                            param_list.append(f"{param_name} : {param_type}")

                    return_type = 'void'
                    if ')' in line[params_end:] and ':' in line[params_end:]:
                        return_type_part = line[params_end:].split(':')[-1].strip()
                        return_type = return_type_part.split(' ')[0]
                        if return_type.endswith('?'):
                            return_type = return_type[:-1]
                        if return_type == 'Unit':
                            return_type = 'void'

                    modifiers = line[:func_start_index-4].strip().split()
                    method_entry = f"{func_name}({', '.join(param_list)}) : {return_type}"
                    if 'override' in modifiers:
                        modifiers.remove('override')
                    if 'public' in modifiers:
                        method_entry = f"+ {method_entry}"
                    elif 'private' in modifiers:
                        method_entry = f"- {method_entry}"
                    elif 'protected' in modifiers:
                        method_entry = f"# {method_entry}"
                    else:
                        method_entry = f"+ {method_entry}"
                    kotlin_methods.append(method_entry)
                i += 1

            return kotlin_classes, kotlin_methods, kotlin_fields
    except IOError as e:
        print(f"Error reading Kotlin file {file_path}: {e}")
        return [], [], []

def extract_java_classes(tree):
    classes = {}
    associations = []
    for _, node in tree:
        if isinstance(node, javalang.tree.ClassDeclaration):
            cls_name = node.name
            classes[cls_name] = {'methods': [], 'fields': [], 'extends': [], 'implements': []}
            if node.extends:
                classes[cls_name]['extends'] = [node.extends.name]
            if node.implements:
                classes[cls_name]['implements'] = [impl.name for impl in node.implements]

            for member in node.body:
                if isinstance(member, javalang.tree.MethodDeclaration):
                    method_name = member.name
                    modifiers = [modifier for modifier in member.modifiers]
                    visibility = get_visibility(modifiers)
                    return_type = member.return_type.name if member.return_type else 'void'
                    params = [f"{param.name} : {param.type.name}" for param in member.parameters]
                    method_entry = f"{visibility} {method_name}({', '.join(params)}) : {return_type}"
                    classes[cls_name]['methods'].append(method_entry)
                elif isinstance(member, javalang.tree.FieldDeclaration):
                    for decl in member.declarators:
                        field_name = decl.name
                        field_type = member.type.name
                        field_entry = f"{field_name} : {field_type}"
                        classes[cls_name]['fields'].append(field_entry)
                        if field_type in classes:
                            associations.append((cls_name, field_type))
    return classes, associations

def get_visibility(modifiers):
    if 'public' in modifiers:
        return '+'
    elif 'private' in modifiers:
        return '-'
    elif 'protected' in modifiers:
        return '#'
    else:
        return '~'

def generate_class_diagram_and_data(path):
    classes = {}
    associations = []

    for root, _, files in os.walk(path):
        for file in files:
            full_path = os.path.join(root, file)

            if file.endswith('.java'):
                tree, file_path = parse_java_file(full_path)
                if tree:
                    java_classes, java_associations = extract_java_classes(tree)
                    classes.update(java_classes)
                    associations.extend(java_associations)

            elif file.endswith('.kt'):
                kotlin_classes, kotlin_methods, kotlin_fields = parse_kotlin_file(full_path)
                for cls_name in kotlin_classes:
                    if cls_name not in classes:
                        classes[cls_name] = {'methods': [], 'fields': [], 'extends': [], 'implements': []}
                    for method in kotlin_methods:
                        classes[cls_name]['methods'].append(method)
                    for field in kotlin_fields:
                        classes[cls_name]['fields'].append(field)

    write_uml_file(classes, associations)
    write_code_data_file(classes)

def write_uml_file(classes, associations):
    with open('uml_diagram.puml', 'w') as file:
        file.write('@startuml\n')
        file.write('left to right direction\n')
        file.write('skinparam classAttributeIconSize 0\n')
        file.write('skinparam nodesep 30\n')  
        file.write('skinparam ranksep 50\n')  
        file.write('skinparam dpi 150\n') 
        file.write('skinparam defaultFontSize 10\n')  
        file.write('skinparam class {\n')
        file.write('  Width 150\n')  
        file.write('}\n')
        
        for cls, details in classes.items():
            file.write(f'class {cls} {{\n')
            for field in details['fields']:
                file.write(f'  {field}\n')
            for method in details['methods']:
                file.write(f'  {method}\n')
            file.write('}\n')
            for base_class in details['extends']:
                file.write(f'{cls} --|> {base_class}\n')
            for interface in details['implements']:
                file.write(f'{cls} ..|> {interface}\n')
        
        for assoc in associations:
            file.write(f'{assoc[0]} --> {assoc[1]}\n')
        
        file.write('@enduml\n')

def write_code_data_file(classes):
    with open('code_data.txt', 'w') as file:
        for cls, details in classes.items():
            file.write(f'Class: {cls}\n')
            file.write('Fields:\n')
            for field in details['fields']:
                file.write(f'  - {field}\n')
            file.write('Methods:\n')
            for method in details['methods']:
                file.write(f'  - {method}\n')
            if details['extends']:
                file.write(f'Extends: {", ".join(details["extends"])}\n')
            if details['implements']:
                file.write(f'Implements: {", ".join(details["implements"])}\n')

if __name__ == "__main__":
    generate_class_diagram_and_data('app/src')
