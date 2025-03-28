export class Tool {
  /**
   * 空校验 null或""都返回true
   */
  public static isEmpty (obj: any) {
    if ((typeof obj === 'string')) {
      return !obj || obj.replace(/\s+/g, "") === ""
    } else {
      ////////////////////////////////
      /* JSON.stringify(obj)报错：
      tool.ts:9
      Uncaught (in promise) TypeError: Converting circular structure to JSON
      --> starting at object with constructor 'ReactiveEffect'
      |     property 'deps' -> object with constructor 'Link'
      --- property 'sub' closes the circle
       */
      ////////////////////////////////
      //return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
      //
      /*I1:
      const obj_JSON = JSON.parse(JSON.stringify(obj));
      return (!obj || obj.length === 0 || obj_JSON === "{}");
       */
      ////////////////////////////////
      //
      /*I2：
      let cache = obj;
      console.log("【cache】：", cache);
      const obj_JSON = JSON.stringify(cache);
      cache = null; // 清空变量，便于垃圾回收机制回收
      return (!obj || obj.length === 0 || obj_JSON === "{}");
       */
      ////////////////////////////////
      //
      /*I3:
      // 声明cache变量，便于匹配是否有循环引用的情况
      let cache : any[];
      const obj_JSON = JSON.stringify(obj, function(key, value) {
        if (typeof value === 'object' && value !== null) {
          if (cache.indexOf(value) !== -1) {
            // 移除
            return;
          }
          // 收集所有的值
          cache.push(value);
        }
        return value;
      });
      //cache = null; // 清空变量，便于垃圾回收机制回收

      return (!obj || obj.length === 0 || obj_JSON === "{}");
       */
      ////////////////////////////////
      //I4：
      let obj_JSON = {};
      try{
        obj_JSON = JSON.stringify(obj);
      } catch (error) {
        console.error("【【error】】：",error);
      }
      return (!obj || obj.length === 0 || obj_JSON === "{}");

    }
  }

  /**
   * 非空校验
   */
  public static isNotEmpty (obj: any) {
    return !this.isEmpty(obj);
  }

  /**
   * 对象复制
   * @param obj
   */
  public static copy (obj: object) {
    if (Tool.isNotEmpty(obj)) {
      return JSON.parse(JSON.stringify(obj));
    }
  }

  /**
   * 使用递归将数组转为树形结构
   * 父ID属性为parent
   */
  public static array2Tree (array: any, parentId: number) {
    if (Tool.isEmpty(array)) {
      return [];
    }
    const result = [];
    for (let i = 0; i < array.length; i++) {
      const c = array[i];
      //console.log(String(c.parent), String(parentId));
      if (String(c.parent) === String(parentId)) {
        result.push(c);

        // 递归查看当前节点对应的子节点
        const children = Tool.array2Tree(array, c.id);
        if (Tool.isNotEmpty(children)) {
          c.children = children;
        }
      }
    }
    return result;
  }

  /**
   * 随机生成[len]长度的[radix]进制数
   * @param len
   * @param radix 默认62
   * @returns {string}
   */
  public static uuid (len: number, radix = 62) {
    const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    const uuid = [];
    radix = radix || chars.length;

    for (let i = 0; i < len; i++) {
      uuid[i] = chars[0 | Math.random() * radix];
    }

    return uuid.join('');
  }
}
