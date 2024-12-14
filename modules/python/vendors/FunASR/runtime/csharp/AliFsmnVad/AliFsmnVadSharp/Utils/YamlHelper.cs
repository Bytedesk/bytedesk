﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Text.Json;
using YamlDotNet.Serialization;

namespace AliFsmnVadSharp.Utils
{
    internal class YamlHelper
    {
        public static T ReadYaml<T>(string yamlFilePath)
        {
            if (!File.Exists(yamlFilePath))
            {
#pragma warning disable CS8603 // 可能返回 null 引用。
                return default(T);
#pragma warning restore CS8603 // 可能返回 null 引用。
            }
            StreamReader yamlReader = File.OpenText(yamlFilePath);
            Deserializer yamlDeserializer = new Deserializer();
            T info = yamlDeserializer.Deserialize<T>(yamlReader);
            yamlReader.Close();
            return info;
        }
    }
}
