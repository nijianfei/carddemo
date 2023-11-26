--无参函数
function hello()
print 'hello'
end

--带参函数，字符串
function test(str)
print '发过来的消息：' .. str
return '发过来的消息：' .. str
end

--带参函数，数组
function test2(str)
print '发过来的消息：' .. str[1]
return '发过来的消息：' .. str[1]
end